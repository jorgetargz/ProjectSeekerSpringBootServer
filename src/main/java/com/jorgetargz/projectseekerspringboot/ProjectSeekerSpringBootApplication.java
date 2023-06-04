package com.jorgetargz.projectseekerspringboot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ProjectSeekerSpringBootApplication is the main entry point for the Spring Boot application.
 * It contains the main method responsible for launching the application.
 * <p>
 * &#064;SpringBootApplication  Indicates that this class is the main configuration class and enables autoconfiguration, component scanning, and configuration properties.
 */
@SpringBootApplication
@Log4j2
@OpenAPIDefinition(
        info = @Info(title = "Project Seeker API", version = "1.0", description = "Project Seeker API", contact = @Contact(name = "Jorge Martín Llorente", email = "jorgetargz@gmail.com")),
        servers = {@Server(url = "http://project-seeker.duckdns.org:8090", description = "Dynamic DNS")},
        externalDocs = @ExternalDocumentation(url = "https://github.com/jorgetargz/ProjectSeeker-TFG", description = "Checkout the GitHub Repository for more information about Project Seeker TFG Project")
)
public class ProjectSeekerSpringBootApplication {
    /**
     * The main method that serves as the entry point for the Spring Boot application.
     * It runs the application using the provided SpringApplication.run() method.
     *
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjectSeekerSpringBootApplication.class, args);
        updateDuckDynamicDNS();
    }

    /**
     * Modify this variable to your server public IP
     */
    private static final String PUBLIC_IP = "34.79.112.43";

    /**
     * This method is used to update the Duck DNS dynamic DNS in order to keep the server reachable
     * at project-seeker.duckdns.org.
     * <p>
     * Note that your Network must be configured to forward the port 8090 to the server IP, or configure the server IP as DMZ in your router.
     */
    private static void updateDuckDynamicDNS() {
        String subdomain = "project-seeker";
        String duckdnsUpdateUrl = "https://www.duckdns.org/update/%s/%s/%s";
        try {
            String token = readTokenFromFile();
            String url = String.format(duckdnsUpdateUrl, subdomain, token, PUBLIC_IP);
            String response = sendGetRequest(url);
            logResponse(response);
        } catch (Exception e) {
            log.error("Error updating Duck DNS dynamic DNS: " + e.getMessage());
        }
    }

    private static String readTokenFromFile() throws IOException {
        String tokenFile = "duckdns_token.json";

        try (InputStream inputStream = ProjectSeekerSpringBootApplication.class.getClassLoader().getResourceAsStream(tokenFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            return jsonObject.get("token").getAsString();
        }
    }


    private static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.getResponseCode();
        return readResponse(con);
    }

    private static String readResponse(HttpURLConnection con) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }
    }

    private static void logResponse(String response) {
        if (!response.contains("OK")) {
            log.error("Error updating Duck DNS dynamic DNS: " + response);
        } else {
            log.info("Duck DNS dynamic DNS updated successfully");
        }
    }
}
