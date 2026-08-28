/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;
import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.service.DomainDecisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/domain") public class DomainDecisionController {
 private final DomainDecisionService service; public DomainDecisionController(DomainDecisionService service){this.service=service;}
 @PostMapping("/decision") public ApiResponse<DomainDecisionService.DecisionResult> assess(@Valid @RequestBody DomainDecisionService.DecisionRequest request){return ApiResponse.ok(service.assess(request));}
}
