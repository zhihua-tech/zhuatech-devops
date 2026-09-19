/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.controller;import cn.zhuatech.devops.common.ApiResponse;import cn.zhuatech.devops.service.ProgressiveDeliveryService;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/advanced/devops") public class ProgressiveDeliveryController{private final ProgressiveDeliveryService service;/**
                                                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                     */
public ProgressiveDeliveryController(ProgressiveDeliveryService service){this.service=service;}/**
                                                                                                                                                                                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                    */
@PostMapping("/progressive-delivery") public ApiResponse<ProgressiveDeliveryService.DeliveryDecision> decide(@Valid @RequestBody ProgressiveDeliveryService.DeliveryRequest request){return ApiResponse.ok(service.decide(request));}}
