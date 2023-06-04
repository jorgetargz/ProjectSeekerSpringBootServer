package com.jorgetargz.projectseekerspringboot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jorgetargz.projectseekerspringboot.config.properties.FirebaseProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Log4j2
@Configuration
public class FirebaseConfig {

    private final FirebaseProperties firebaseProps;

    @Autowired
    public FirebaseConfig(FirebaseProperties firebaseProps) {
        this.firebaseProps = firebaseProps;
    }

    @Primary
    @Bean
    public FirebaseApp getfirebaseApp() throws IOException {
        ClassPathResource serviceAccount = new ClassPathResource(firebaseProps.getServiceAccount());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        FirebaseApp app = FirebaseApp.getInstance();
        log.info("FirebaseApp: {}", app.getName());
        return app;
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getfirebaseApp());
    }

    @Bean
    public FirebaseMessaging getMessaging() throws IOException {
        return FirebaseMessaging.getInstance(getfirebaseApp());
    }

}
