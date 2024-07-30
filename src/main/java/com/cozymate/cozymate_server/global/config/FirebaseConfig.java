package com.cozymate.cozymate_server.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
            new ClassPathResource("firebase-service-account.json").getInputStream());

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
            .setCredentials(googleCredentials).build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);

        return FirebaseMessaging.getInstance(app);
    }
}