/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;

import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.time.*;

@Service
public class ReleaseManagementService {
    private final ReleaseCandidateRepository releases;private final AuditLogRepository audits;
    public ReleaseManagementService(ReleaseCandidateRepository releases,AuditLogRepository audits){
        this.releases=releases;this.audits=audits;
    }

    public List<ReleaseCandidate> list(){return releases.findAllByOrderByUpdatedAtDesc();}

    @Transactional
    public ReleaseCandidate create(CreateRequest request){
        if(releases.findByReleaseNo(request.releaseNo()).isPresent())throw conflict("发布单号已存在");
        var item=releases.save(new ReleaseCandidate(request.releaseNo(),request.applicationCode(),
            request.commitSha().toLowerCase(Locale.ROOT),request.artifactDigest().toLowerCase(Locale.ROOT),
            request.environment(),request.testPassRate(),request.criticalVulnerabilities(),request.rollbackVersion(),
            request.changeTicket(),request.scheduledAt(),request.emergencyApproval()));
        audit("创建发布候选",item,request.applicationCode());return item;
    }

    public GateResult gate(Long id){
        var item=get(id);List<String> blockers=new ArrayList<>();int score=100;
        if(item.getTestPassRate()<95){score-=35;blockers.add("测试通过率低于95%");}
        if(item.getCriticalVulnerabilities()>0){score-=60;blockers.add("存在严重安全漏洞");}
        if(item.getRollbackVersion().isBlank()){score-=40;blockers.add("未配置可验证的回滚版本");}
        if(!Set.of("STAGING","PRODUCTION").contains(item.getEnvironment())){score-=20;blockers.add("发布环境不受控");}
        if("PRODUCTION".equals(item.getEnvironment())&&!item.getChangeTicket().matches("CHG-[A-Z0-9-]{4,40}")){
            score-=50;blockers.add("生产发布缺少有效变更单号");
        }
        var day=item.getScheduledAt().getDayOfWeek();
        if("PRODUCTION".equals(item.getEnvironment())
                && Set.of(DayOfWeek.SATURDAY,DayOfWeek.SUNDAY).contains(day)&&!item.isEmergencyApproval()){
            score-=50;blockers.add("计划时间处于周末冻结窗口且未取得紧急放行");
        }
        return new GateResult(blockers.isEmpty()?"READY":"BLOCKED",Math.max(0,score),
            item.getReleaseNo(),List.copyOf(blockers));
    }

    @Transactional
    public ReleaseCandidate submit(Long id){
        var item=get(id);require(item,"DRAFT","只有草稿可以提交审批");
        if(!"READY".equals(gate(id).decision()))throw conflict("质量门禁未通过，禁止提交审批");
        item.submit();audit("提交发布审批",item,"质量门禁通过");return item;
    }

    @Transactional
    public ReleaseCandidate approve(Long id,String remark){
        var item=get(id);require(item,"PENDING_APPROVAL","只有待审批发布可以批准");
        item.approve();audit("批准发布",item,remark);return item;
    }

    @Transactional
    public ReleaseCandidate deploy(Long id,DeployRequest request){
        var item=get(id);require(item,"APPROVED","仅已批准发布允许部署");
        if(!item.getArtifactDigest().equalsIgnoreCase(request.artifactDigest()))throw conflict("部署制品哈希与审批版本不一致");
        if(!request.healthCheckPassed())throw conflict("健康检查未通过，部署自动中止");
        item.deploy();audit("生产部署",item,request.strategy()+" · 健康检查通过");return item;
    }

    @Transactional
    public ReleaseCandidate rollback(Long id,RollbackRequest request){
        var item=get(id);require(item,"DEPLOYED","仅已部署版本允许回滚");
        if(!item.getRollbackVersion().equals(request.targetVersion()))throw conflict("回滚目标与批准方案不一致");
        item.rollback();audit("版本回滚",item,request.reason());return item;
    }

    public Dashboard dashboard(){
        long total=releases.count(),pending=releases.countByState("PENDING_APPROVAL"),
            deployed=releases.countByState("DEPLOYED"),rolledBack=releases.countByState("ROLLED_BACK");
        long blocked=releases.findAll().stream().filter(item->"BLOCKED".equals(gate(item.getId()).decision())).count();
        return new Dashboard(total,pending,deployed,rolledBack,blocked);
    }

    private ReleaseCandidate get(Long id){return releases.findById(id).orElseThrow(()->
        new ResponseStatusException(HttpStatus.NOT_FOUND,"发布候选不存在"));}
    private void require(ReleaseCandidate item,String state,String message){if(!state.equals(item.getState()))throw conflict(message);}
    private ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
    private void audit(String action,ReleaseCandidate item,String detail){
        var auth=SecurityContextHolder.getContext().getAuthentication();
        audits.save(new AuditLog("RELEASE",action,item.getReleaseNo(),auth==null?"system":auth.getName(),detail));
    }

    public record CreateRequest(@NotBlank @Size(max=40) String releaseNo,@NotBlank @Size(max=60) String applicationCode,
        @Pattern(regexp="(?i)[0-9a-f]{40}") String commitSha,
        @Pattern(regexp="(?i)sha256:[0-9a-f]{64}") String artifactDigest,
        @Pattern(regexp="STAGING|PRODUCTION") String environment,
        @DecimalMin("0") @DecimalMax("100") double testPassRate,@PositiveOrZero int criticalVulnerabilities,
        @NotBlank @Size(max=60) String rollbackVersion,
        @NotBlank @Size(max=50) String changeTicket,@NotNull LocalDateTime scheduledAt,
        boolean emergencyApproval){}
    public record DeployRequest(@NotBlank @Pattern(regexp="(?i)sha256:[0-9a-f]{64}") String artifactDigest,
        @NotBlank @Pattern(regexp="ROLLING|BLUE_GREEN|CANARY") String strategy,boolean healthCheckPassed){}
    public record RollbackRequest(@NotBlank String targetVersion,@NotBlank @Size(max=300) String reason){}
    public record GateResult(String decision,int score,String releaseNo,List<String> blockers){}
    public record Dashboard(long total,long pendingApproval,long deployed,long rolledBack,long blocked){}
}
