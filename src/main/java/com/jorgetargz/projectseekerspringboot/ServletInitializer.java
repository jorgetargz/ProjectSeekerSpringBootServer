package com.jorgetargz.projectseekerspringboot;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer is a class responsible for configuring the Servlet container when
 * deploying the Spring Boot application as a WAR file.
 * <p>
 * It extends SpringBootServletInitializer and overrides the configure method to
 * set up the application's main class.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configures the SpringApplicationBuilder for the application.
     * This method is called when the application is deployed as a WAR file.
     * It specifies the main class (ProjectSeekerSpringBootApplication) as the source.
     *
     * @param application The SpringApplicationBuilder used for configuration.
     * @return The configured SpringApplicationBuilder instance.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProjectSeekerSpringBootApplication.class);
    }

}
