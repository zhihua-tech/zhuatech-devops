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
import java.time.LocalDate;
import java.util.*;
@Service
public class EnterpriseControlService {
    private final EnterpriseControlRepository controls; private final ControlDocumentRepository documents;
    private final AuditLogRepository audits;
    public EnterpriseControlService(EnterpriseControlRepository controls,ControlDocumentRepository documents,AuditLogRepository audits){
        this.controls=controls;this.documents=documents;this.audits=audits;
    }
    public List<EnterpriseControl> list(String state){
        return state==null||state.isBlank()?controls.findAllByOrderByUpdatedAtDesc():controls.findByStateOrderByUpdatedAtDesc(state);
    }
    public Summary summary(){
        var all=controls.findAll();Map<String,Long> states=new LinkedHashMap<>(),sync=new LinkedHashMap<>();
        all.forEach(item->{states.merge(item.getState(),1L,Long::sum);sync.merge(item.getSyncState(),1L,Long::sum);});
        long overdue=all.stream().filter(item->!Set.of("COMPLETED","REJECTED").contains(item.getState())&&item.getDueDate().isBefore(LocalDate.now())).count();
        return new Summary(all.size(),overdue,states,sync);
    }
    @Transactional public EnterpriseControl create(CreateRequest request){
        var existing=controls.findByIdempotencyKey(request.idempotencyKey());
        if(existing.isPresent())return existing.get();
        if(controls.findByControlNo(request.controlNo()).isPresent())throw conflict("企业控制单号已存在");
        var item=controls.save(new EnterpriseControl(request.controlNo(),request.organizationCode(),request.fiscalPeriod(),
            request.controlType(),request.subjectNo(),request.subjectName(),request.assignee(),request.riskLevel(),
            request.dueDate(),request.externalSystem(),request.externalRef(),request.idempotencyKey()));
        audit("创建企业控制项",item,request.subjectName());return item;
    }
    @Transactional public EnterpriseControl submit(Long id){
        var item=get(id);requireState(item,"DRAFT","只有草稿可以提交");item.submit();audit("提交复核",item,"进入管理员复核");return item;
    }
    @Transactional public EnterpriseControl review(Long id,ReviewRequest request){
        var item=get(id);requireState(item,"PENDING_REVIEW","只有待复核事项可以审批");
        if("APPROVE".equals(request.decision()))item.approve();else if("REJECT".equals(request.decision()))item.reject();
        else throw bad("复核决定仅支持 APPROVE 或 REJECT");
        audit("企业复核",item,request.decision()+" · "+Objects.toString(request.remark(),""));return item;
    }
    @Transactional public EnterpriseControl complete(Long id){
        var item=get(id);requireState(item,"APPROVED","只有已批准事项可以办结");
        if(item.getDocumentCount()==0)throw conflict("办结前必须登记至少一份凭证附件");
        item.complete();audit("业务办结",item,"凭证数量 "+item.getDocumentCount());return item;
    }
    @Transactional public ControlDocument registerDocument(Long id,DocumentRequest request){
        var item=get(id);if(Set.of("COMPLETED","REJECTED").contains(item.getState()))throw conflict("终态事项不能补充附件");
        var auth=SecurityContextHolder.getContext().getAuthentication();
        var doc=documents.save(new ControlDocument(id,request.fileName(),request.mediaType(),request.sizeBytes(),
            request.sha256().toLowerCase(Locale.ROOT),request.storageKey(),auth==null?"system":auth.getName()));
        item.addDocument();audit("登记附件",item,request.fileName());return doc;
    }
    public List<ControlDocument> documents(Long id){get(id);return documents.findByControlIdOrderByCreatedAtDesc(id);}
    @Transactional public EnterpriseControl sync(Long id,SyncRequest request){
        var item=get(id);if(!Set.of("APPROVED","COMPLETED").contains(item.getState()))throw conflict("仅批准或办结事项允许同步");
        item.sync(request.success()?"SYNCED":"FAILED",request.externalRef());
        audit("外部同步",item,item.getSyncState()+" · "+Objects.toString(request.message(),""));return item;
    }
    private EnterpriseControl get(Long id){return controls.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"企业控制项不存在"));}
    private void requireState(EnterpriseControl item,String state,String message){if(!state.equals(item.getState()))throw conflict(message);}
    private ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
    private ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
    private void audit(String action,EnterpriseControl item,String detail){
        var auth=SecurityContextHolder.getContext().getAuthentication();
        audits.save(new AuditLog("ENTERPRISE",action,item.getControlNo(),auth==null?"system":auth.getName(),detail));
    }
    public record CreateRequest(
        @NotBlank @Size(max=40) String controlNo,
        @NotBlank @Size(max=40) String organizationCode,
        @NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])") String fiscalPeriod,
        @NotBlank @Size(max=40) String controlType,
        @NotBlank @Size(max=60) String subjectNo,
        @NotBlank @Size(max=120) String subjectName,
        @NotBlank @Size(max=50) String assignee,
        @NotBlank @Size(max=20) String riskLevel,
        @NotNull LocalDate dueDate,
        @Size(max=40) String externalSystem,
        @Size(max=100) String externalRef,
        @NotBlank @Size(max=80) String idempotencyKey){}
    public record ReviewRequest(@NotBlank String decision,@Size(max=300) String remark){}
    public record DocumentRequest(@NotBlank @Size(max=160) String fileName,@NotBlank @Size(max=100) String mediaType,
        @Positive long sizeBytes,@NotBlank @Pattern(regexp="(?i)[0-9a-f]{64}") String sha256,
        @NotBlank @Size(max=120) String storageKey){}
    public record SyncRequest(boolean success,@Size(max=100) String externalRef,@Size(max=300) String message){}
    public record Summary(long total,long overdue,Map<String,Long> states,Map<String,Long> syncStates){}
}
