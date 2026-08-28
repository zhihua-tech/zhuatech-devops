/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import java.util.*;

@Configuration
@Profile("prod")
public class ProductionSafetyConfig {
    private final Environment environment;
    public ProductionSafetyConfig(Environment environment) { this.environment = environment; }

    @PostConstruct
    void verifyProductionSecrets() {
        Map<String, String> unsafe = new LinkedHashMap<>();
        unsafe.put("ADMIN_PASSWORD", "admin123");
        unsafe.put("OPERATOR_PASSWORD", "operator123");
        unsafe.put("DB_PASSWORD", "change-me");
        List<String> violations = new ArrayList<>();
        unsafe.forEach((key, forbidden) -> {
            String value = System.getenv(key);
            if (value == null || value.isBlank() || forbidden.equals(value)) violations.add(key);
        });
        String origins = environment.getProperty("zhuatech.security.allowed-origins", "");
        if (origins.isBlank() || origins.contains("localhost") || origins.contains("127.0.0.1")) {
            violations.add("ALLOWED_ORIGINS");
        }
        if (!violations.isEmpty()) {
            throw new IllegalStateException("生产环境安全配置缺失或仍为默认值: " + String.join(", ", violations));
        }
    }
}
