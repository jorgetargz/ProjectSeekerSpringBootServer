package com.jorgetargz.projectseekerspringboot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.valid-application-roles")
@Data
public class AppRolesProperties {
    private String user;
    private String client;
    private String freelancer;
    private String superAdmin;

    public List<String> getAllRoles() {
        return List.of(user, client, freelancer, superAdmin);
    }

    public boolean isRoleAvailable(String role) {
        return role.equals(user) || role.equals(client) || role.equals(freelancer) || role.equals(superAdmin);
    }
}
