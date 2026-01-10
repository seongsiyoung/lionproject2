package com.example.lionproject2backend.auth.cookie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    private String refreshName;
    private String refreshPath;
    private String refreshSameSite;
    private boolean refreshSecure;
}