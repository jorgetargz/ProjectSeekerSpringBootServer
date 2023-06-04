package com.jorgetargz.projectseekerspringboot.utils;

import com.jorgetargz.projectseekerspringboot.config.properties.CookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.concurrent.TimeUnit;


@Service
public class CookieUtils {

    private final HttpServletRequest httpServletRequest;

    private final HttpServletResponse httpServletResponse;

    private final CookieProperties cookieProps;

    @Autowired
    public CookieUtils(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, CookieProperties cookieProps) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.cookieProps = cookieProps;
    }

    public Cookie getCookie(String name) {
        return WebUtils.getCookie(httpServletRequest, name);
    }

    public void setCookie(String name, String value) {
        int expiresInMinutes = cookieProps.getMaxAgeInMinutes();
        int expiresInSeconds = (int) TimeUnit.MINUTES.toSeconds(expiresInMinutes);
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(cookieProps.isSecure());
        cookie.setPath(cookieProps.getPath());
        cookie.setMaxAge(expiresInSeconds);
        httpServletResponse.addCookie(cookie);
    }

    public void deleteCookie(String name) {
        int expiresInSeconds = 0;
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(expiresInSeconds);
        httpServletResponse.addCookie(cookie);
    }

}