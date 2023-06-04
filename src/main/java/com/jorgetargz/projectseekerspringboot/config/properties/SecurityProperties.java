package com.jorgetargz.projectseekerspringboot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This class is used to configure the security properties.
 * <p>
 * The properties are loaded from the application.yaml file.
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    /**
     * cookieProps: This property is used to configure the cookie properties.
     */
    private CookieProperties cookieProps;

    /**
     * firebaseProps: This property is used to configure the firebase properties.
     */
    private FirebaseProperties firebaseProps;

    /**
     * privateApis: This property is used to configure the private APIs paths the requests to this paths must be authenticated.
     */
    private List<String> privateApis;

    /**
     * superAdmins: This property is used to configure the super admin users.
     */
    private List<String> superAdmins;

    /**
     * validApplicationRoles: This property is used to configure the valid application roles.
     */
    private AppRolesProperties validApplicationRoles;

}
