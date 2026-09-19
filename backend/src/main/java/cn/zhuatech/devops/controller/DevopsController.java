/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;
import cn.zhuatech.devops.common.ApiResponse;
import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.service.DevopsService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api")
public class DevopsController {
    private final DevopsService service; /**
                                          * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                          */
public DevopsController(DevopsService service){this.service=service;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/public/about") ApiResponse<Map<String,Object>> about(){return ApiResponse.ok(service.about());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/catalog") ApiResponse<DevopsService.CatalogView> catalog(){return ApiResponse.ok(service.catalog());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/dashboard") ApiResponse<DevopsService.Dashboard> dashboard(){return ApiResponse.ok(service.dashboard());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/records") ApiResponse<List<BusinessRecord>> records(@RequestParam(required=false) String module){return ApiResponse.ok(service.list(module));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/records/search") ApiResponse<DevopsService.PageView> search(@RequestParam(required=false) String module,@RequestParam(required=false) String status,@RequestParam(required=false) String riskLevel,@RequestParam(required=false) String keyword,@RequestParam(required=false) Boolean overdue,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return ApiResponse.ok(service.search(module,status,riskLevel,keyword,overdue,page,size));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/records/export.csv") ResponseEntity<byte[]> export(@RequestParam(required=false) String module,@RequestParam(required=false) String status,@RequestParam(required=false) String riskLevel,@RequestParam(required=false) String keyword,@RequestParam(required=false) Boolean overdue){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=records.csv").contentType(new MediaType("text","csv",StandardCharsets.UTF_8)).body(service.exportCsv(module,status,riskLevel,keyword,overdue).getBytes(StandardCharsets.UTF_8));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/records/{id}") ApiResponse<BusinessRecord> detail(@PathVariable Long id){return ApiResponse.ok(service.detail(id));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/records/{id}/timeline") ApiResponse<List<AuditLog>> timeline(@PathVariable Long id){return ApiResponse.ok(service.timeline(id));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/records/{id}/comments") ApiResponse<BusinessRecord> comment(@PathVariable Long id,@Valid @RequestBody DevopsService.CommentRequest request){return ApiResponse.ok(service.comment(id,request));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/sla-summary") ApiResponse<DevopsService.SlaSummary> sla(){return ApiResponse.ok(service.slaSummary());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/records") ApiResponse<BusinessRecord> create(@Valid @RequestBody DevopsService.RecordRequest request){return ApiResponse.ok(service.create(request));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/records/{id}") ApiResponse<BusinessRecord> update(@PathVariable Long id,@Valid @RequestBody DevopsService.RecordRequest request){return ApiResponse.ok(service.update(id,request));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/records/{id}/actions") ApiResponse<BusinessRecord> action(@PathVariable Long id,@Valid @RequestBody DevopsService.ActionRequest request){return ApiResponse.ok(service.action(id,request));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @DeleteMapping("/records/{id}") ApiResponse<Void> delete(@PathVariable Long id){service.delete(id);return ApiResponse.ok(null);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/audit-logs") ApiResponse<List<AuditLog>> audits(){return ApiResponse.ok(service.auditLogs());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/admin/settings") ApiResponse<Map<String,String>> settings(){return ApiResponse.ok(service.settings());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PutMapping("/admin/settings") ApiResponse<Map<String,String>> updateSettings(@RequestBody Map<String,String> values){return ApiResponse.ok(service.updateSettings(values));}
}
