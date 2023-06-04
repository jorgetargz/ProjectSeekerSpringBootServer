package com.jorgetargz.projectseekerspringboot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to configure the cookie properties.
 * <p>
 * The properties are loaded from the application.yaml file.
 */
@Configuration
@ConfigurationProperties(prefix = "security.cookie-props")
@Data
public class CookieProperties {

    /**
     * path: Specifies a path for the cookie to which the client should return the cookie.
     * The cookie is visible to all the pages in the directory you specify,
     * and all the pages in that directory's subdirectories.
     * A cookie's path must include the servlet that set the cookie,
     * for example, /catalog, which makes the cookie visible to all
     * directories on the server under /catalog
     */
    private String path;

    /**
     * secure: This property is used to configure the secure attribute of the cookie.
     * if true, sends the cookie from the browser to the server only when using a secure protocol; if false, sent on any protocol
     */
    private boolean secure;

    /**
     * maxAgeInMinutes: Sets the maximum age of the cookie in minutes.
     * <p>
     * A positive value indicates that the cookie will expire after that many minutes have passed.
     * Note that the value is the maximum age when the cookie will expire, not the cookie's current age.
     * <p>
     * A negative value means that the cookie is not stored persistently and will be deleted when
     * the Web browser exits.
     * <p>
     * A zero value causes the cookie to be deleted.
     */
    private int maxAgeInMinutes;
}
