/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class DomainDecisionService {
 public DecisionResult assess(DecisionRequest request) { int score=100;List<String> actions=new ArrayList<>();if(request.testPassRate()<95){score-=30;actions.add("修复失败测试并重新执行回归");}if(request.criticalVulnerabilities()>0){score-=60;actions.add("阻断严重漏洞版本发布");}if(request.deploymentSuccessRate()<98){score-=20;actions.add("提升部署成功率并验证环境一致性");}if(request.changeFailureRate()>15){score-=25;actions.add("降低变更失败率");}if(request.leadTimeHours()>72){score-=10;actions.add("优化交付前置时间");}if(!request.approvalPassed()){score-=40;actions.add("完成发布审批");}if(!request.rollbackReady()){score-=35;actions.add("准备并演练回滚方案");}return result(score,actions,"READY_TO_RELEASE","HOLD","BLOCKED",Map.of("testPassRate",request.testPassRate(),"deploymentSuccessRate",request.deploymentSuccessRate(),"changeFailureRate",request.changeFailureRate(),"leadTimeHours",request.leadTimeHours())); }
 private DecisionResult result(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=80?good:score>=50?warn:bad;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 private DecisionResult riskResult(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=70?bad:score>=40?warn:good;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 public record DecisionRequest(
        @NotBlank String releaseNo,
        @DecimalMin("0") @DecimalMax("100") double testPassRate,
        @PositiveOrZero int criticalVulnerabilities,
        @DecimalMin("0") @DecimalMax("100") double deploymentSuccessRate,
        @DecimalMin("0") @DecimalMax("100") double changeFailureRate,
        @PositiveOrZero int leadTimeHours,
        boolean approvalPassed,
        boolean rollbackReady) {}
 public record DecisionResult(String decision,int score,Map<String,Object> metrics,List<String> actions) {}
}
