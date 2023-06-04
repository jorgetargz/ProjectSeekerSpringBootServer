package com.jorgetargz.projectseekerspringboot.services.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.jorgetargz.projectseekerspringboot.services.NotificationsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;

@Log4j2
@Service
public class NotificationsServiceImpl implements NotificationsService {

    private final FirebaseMessaging firebaseMessaging;

    public NotificationsServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public void sendNotification(Map<String,String> devices, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        for (Map.Entry<String, String> entry : devices.entrySet()) {
            Message message = Message.builder()
                    .setToken(entry.getValue())
                    .setNotification(notification)
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("Error sending notification to device: " + entry.getKey(), e);
            }
        }

    }
}
