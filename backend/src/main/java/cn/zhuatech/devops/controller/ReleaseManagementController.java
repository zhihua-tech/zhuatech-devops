/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;

import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.model.ReleaseCandidate;
import cn.zhuatech.devops.service.ReleaseManagementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/devops/releases")
public class ReleaseManagementController {
    private final ReleaseManagementService service;
    public ReleaseManagementController(ReleaseManagementService service){this.service=service;}
    @GetMapping ApiResponse<List<ReleaseCandidate>> list(){return ApiResponse.ok(service.list());}
    @PostMapping ApiResponse<ReleaseCandidate> create(@Valid @RequestBody ReleaseManagementService.CreateRequest request){return ApiResponse.ok(service.create(request));}
    @GetMapping("/{id}/gate") ApiResponse<ReleaseManagementService.GateResult> gate(@PathVariable Long id){return ApiResponse.ok(service.gate(id));}
    @PostMapping("/{id}/submit") ApiResponse<ReleaseCandidate> submit(@PathVariable Long id){return ApiResponse.ok(service.submit(id));}
    @PostMapping("/{id}/deploy") ApiResponse<ReleaseCandidate> deploy(@PathVariable Long id,@Valid @RequestBody ReleaseManagementService.DeployRequest request){return ApiResponse.ok(service.deploy(id,request));}
    @PostMapping("/{id}/rollback") ApiResponse<ReleaseCandidate> rollback(@PathVariable Long id,@Valid @RequestBody ReleaseManagementService.RollbackRequest request){return ApiResponse.ok(service.rollback(id,request));}
    @GetMapping("/dashboard") ApiResponse<ReleaseManagementService.Dashboard> dashboard(){return ApiResponse.ok(service.dashboard());}
}
