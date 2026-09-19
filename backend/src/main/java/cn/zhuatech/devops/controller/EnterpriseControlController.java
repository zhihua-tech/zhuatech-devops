/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;

import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.service.EnterpriseControlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseControlController {
    private final EnterpriseControlService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseControlController(EnterpriseControlService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/controls")
    ApiResponse<List<EnterpriseControl>> list(@RequestParam(required=false) String state,
            @RequestParam(required=false) String organizationCode,
            @RequestParam(required=false) String fiscalPeriod) {
        return ApiResponse.ok(service.list(state, organizationCode, fiscalPeriod));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/summary")
    ApiResponse<EnterpriseControlService.Summary> summary(
            @RequestParam(required=false) String organizationCode,
            @RequestParam(required=false) String fiscalPeriod) {
        return ApiResponse.ok(service.summary(organizationCode, fiscalPeriod));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/workbench")
    ApiResponse<EnterpriseControlService.Workbench> workbench(
            @RequestParam(required=false) String organizationCode,
            @RequestParam(required=false) String fiscalPeriod) {
        return ApiResponse.ok(service.workbench(organizationCode, fiscalPeriod));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/period-status")
    ApiResponse<EnterpriseControlService.PeriodStatus> periodStatus(
            @RequestParam String organizationCode, @RequestParam String fiscalPeriod) {
        return ApiResponse.ok(service.periodStatus(organizationCode, fiscalPeriod));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/controls")
    ApiResponse<EnterpriseControl> create(
            @Valid @RequestBody EnterpriseControlService.CreateRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/controls/{id}/submit")
    ApiResponse<EnterpriseControl> submit(@PathVariable Long id) {
        return ApiResponse.ok(service.submit(id));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/controls/bulk-submit")
    ApiResponse<EnterpriseControlService.BatchResult> bulkSubmit(
            @Valid @RequestBody EnterpriseControlService.BatchRequest request) {
        return ApiResponse.ok(service.bulkSubmit(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/controls/{id}/complete")
    ApiResponse<EnterpriseControl> complete(@PathVariable Long id) {
        return ApiResponse.ok(service.complete(id));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/controls/{id}/documents")
    ApiResponse<ControlDocument> document(@PathVariable Long id,
            @Valid @RequestBody EnterpriseControlService.DocumentRequest request) {
        return ApiResponse.ok(service.registerDocument(id, request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/controls/{id}/documents")
    ApiResponse<List<ControlDocument>> documents(@PathVariable Long id) {
        return ApiResponse.ok(service.documents(id));
    }
}
