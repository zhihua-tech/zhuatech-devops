/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;

import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.service.ProductionDeploymentAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/devops")
public class ProductionDeploymentAuthorizationController {
    private final ProductionDeploymentAuthorizationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ProductionDeploymentAuthorizationController(ProductionDeploymentAuthorizationService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/production-deployment-authorization")
    public ApiResponse<ProductionDeploymentAuthorizationService.Assessment> assess(
            @Valid @RequestBody ProductionDeploymentAuthorizationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
