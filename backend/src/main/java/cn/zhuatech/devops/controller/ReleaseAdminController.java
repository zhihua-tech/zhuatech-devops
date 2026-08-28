/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;

import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.model.ReleaseCandidate;
import cn.zhuatech.devops.service.ReleaseManagementService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/devops/releases")
@Validated
public class ReleaseAdminController {
    private final ReleaseManagementService service;
    public ReleaseAdminController(ReleaseManagementService service){this.service=service;}
    @PostMapping("/{id}/approve") ApiResponse<ReleaseCandidate> approve(@PathVariable Long id,
        @RequestParam @NotBlank String remark){return ApiResponse.ok(service.approve(id,remark));}
}
