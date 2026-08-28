/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;
import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.service.EnterpriseControlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/enterprise")
public class EnterpriseControlController {
    private final EnterpriseControlService service;
    public EnterpriseControlController(EnterpriseControlService service){this.service=service;}
    @GetMapping("/controls") ApiResponse<List<EnterpriseControl>> list(@RequestParam(required=false) String state){return ApiResponse.ok(service.list(state));}
    @GetMapping("/summary") ApiResponse<EnterpriseControlService.Summary> summary(){return ApiResponse.ok(service.summary());}
    @PostMapping("/controls") ApiResponse<EnterpriseControl> create(@Valid @RequestBody EnterpriseControlService.CreateRequest request){return ApiResponse.ok(service.create(request));}
    @PostMapping("/controls/{id}/submit") ApiResponse<EnterpriseControl> submit(@PathVariable Long id){return ApiResponse.ok(service.submit(id));}
    @PostMapping("/controls/{id}/complete") ApiResponse<EnterpriseControl> complete(@PathVariable Long id){return ApiResponse.ok(service.complete(id));}
    @PostMapping("/controls/{id}/documents") ApiResponse<ControlDocument> document(@PathVariable Long id,@Valid @RequestBody EnterpriseControlService.DocumentRequest request){return ApiResponse.ok(service.registerDocument(id,request));}
    @GetMapping("/controls/{id}/documents") ApiResponse<List<ControlDocument>> documents(@PathVariable Long id){return ApiResponse.ok(service.documents(id));}
}
