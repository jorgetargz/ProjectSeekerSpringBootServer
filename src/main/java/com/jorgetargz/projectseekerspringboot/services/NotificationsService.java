package com.jorgetargz.projectseekerspringboot.services;

import java.util.Map;

public interface NotificationsService {

    void sendNotification(Map<String,String> devices, String title, String body);

}
