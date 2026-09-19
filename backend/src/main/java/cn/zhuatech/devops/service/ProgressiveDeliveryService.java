/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import java.math.*;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class ProgressiveDeliveryService{
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public DeliveryDecision decide(@Valid DeliveryRequest request){List<String>reasons=new ArrayList<>();boolean rollback=false;
  if(request.canary().sampleCount()<request.minimumSamples())reasons.add("金丝雀样本量不足");
  if(request.canary().errorRate().subtract(request.baseline().errorRate()).compareTo(request.maxErrorRateIncrease())>0){rollback=true;reasons.add("错误率劣化超过阈值");}
  if(request.canary().p95LatencyMs()-request.baseline().p95LatencyMs()>request.maxP95LatencyIncreaseMs()){rollback=true;reasons.add("P95延迟劣化超过阈值");}
  if(request.canary().availability().compareTo(request.minimumAvailability())<0){rollback=true;reasons.add("可用性低于发布门槛");}
  String decision;int next;if(rollback){decision="ROLLBACK";next=0;}else if(!request.approvalPassed()||request.canary().sampleCount()<request.minimumSamples()){decision="HOLD";next=request.currentTrafficPercent();}else{next=Math.min(100,request.currentTrafficPercent()+request.trafficStepPercent());decision=next==100?"PROMOTE_FULL":"PROMOTE_STEP";}
  return new DeliveryDecision(decision,request.currentTrafficPercent(),next,reasons,Map.of("errorRateDelta",request.canary().errorRate().subtract(request.baseline().errorRate()),"p95LatencyDeltaMs",request.canary().p95LatencyMs()-request.baseline().p95LatencyMs(),"canaryAvailability",request.canary().availability()));
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DeliveryRequest(@NotBlank String releaseNo,@Min(0) @Max(100) int currentTrafficPercent,@Min(1) @Max(100) int trafficStepPercent,@Positive long minimumSamples,@NotNull @DecimalMin("0") BigDecimal maxErrorRateIncrease,@PositiveOrZero long maxP95LatencyIncreaseMs,@NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal minimumAvailability,boolean approvalPassed,@NotNull @Valid Metrics baseline,@NotNull @Valid Metrics canary){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Metrics(@NotNull @DecimalMin("0") BigDecimal errorRate,@PositiveOrZero long p95LatencyMs,@NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal availability,@PositiveOrZero long sampleCount){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record DeliveryDecision(String decision,int currentTrafficPercent,int nextTrafficPercent,List<String>reasons,Map<String,Object>metricDeltas){}
}
