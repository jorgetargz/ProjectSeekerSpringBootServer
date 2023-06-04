package com.jorgetargz.projectseekerspringboot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to configure the firebase properties.
 * <p>
 * The properties are loaded from the application.yaml file.
 */
@Configuration
@ConfigurationProperties(prefix = "security.firebase-props")
@Data
public class FirebaseProperties {

    /**
     * serviceAccount: This property is used to configure the firebase service account file path.
     */
    private String serviceAccount;

    /**
     * enableCheckSessionRevoked: This property is a boolean indicating whether to check
     * if the cookie was explicitly revoked or if the user is disabled.
     */
    private boolean enableCheckSessionRevoked;

}
