/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ProductionDeploymentAuthorizationServiceTest {
    private final ProductionDeploymentAuthorizationService service = new ProductionDeploymentAuthorizationService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void authorizesControlledProductionDeployment() {
        var result = service.assess(request(true, true, true));
        assertThat(result.decision()).isEqualTo(ProductionDeploymentAuthorizationService.Decision.DEPLOY);
        assertThat(result.blockers()).isEmpty();
        assertThat(result.actions()).isEmpty();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void sendsNonBlockingPreparationToReview() {
        var result = service.assess(request(false, false, false));
        assertThat(result.decision()).isEqualTo(ProductionDeploymentAuthorizationService.Decision.REVIEW);
        assertThat(result.actions()).hasSize(3);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void blocksUncontrolledProductionDeployment() {
        var result = service.assess(new ProductionDeploymentAuthorizationService.Request("DEP-003", false, false,
                false, false, true, false, false, false, true, true, false, true, false, false));
        assertThat(result.decision()).isEqualTo(ProductionDeploymentAuthorizationService.Decision.BLOCKED);
        assertThat(result.blockers()).hasSize(10);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ProductionDeploymentAuthorizationService.Request request(boolean canary, boolean observability, boolean notice) {
        return new ProductionDeploymentAuthorizationService.Request("DEP-001", true, true, true, true,
                false, false, true, true, canary, observability, true, notice, true, true);
    }
}
