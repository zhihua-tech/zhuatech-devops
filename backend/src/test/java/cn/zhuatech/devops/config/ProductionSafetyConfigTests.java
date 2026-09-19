/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.config;

import org.junit.jupiter.api.*;
import org.springframework.mock.env.MockEnvironment;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ProductionSafetyConfigTests {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void productionProfileRejectsMissingSecretsAndLocalOrigins() {
        var environment = new MockEnvironment()
            .withProperty("zhuatech.security.allowed-origins", "http://localhost:5173");
        var config = new ProductionSafetyConfig(environment);
        var error = Assertions.assertThrows(IllegalStateException.class, config::verifyProductionSecrets);
        Assertions.assertTrue(error.getMessage().contains("ADMIN_PASSWORD"));
        Assertions.assertTrue(error.getMessage().contains("ALLOWED_ORIGINS"));
    }
}
