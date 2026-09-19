/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;
import cn.zhuatech.devops.domain.DomainCatalog;
import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class DevopsService {
    private final BusinessRecordRepository records; private final AuditLogRepository audits;
    private final SystemSettingRepository settings; private final DomainCatalog catalog;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public DevopsService(BusinessRecordRepository records,AuditLogRepository audits,SystemSettingRepository settings,DomainCatalog catalog){
        this.records=records;this.audits=audits;this.settings=settings;this.catalog=catalog;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Map<String,Object> about(){return Map.of("product",catalog.systemName(),"company","上海如静知华信息科技有限公司","website","https://www.zhuatech.cn/","license","仅限个人非商业学习交流");}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public CatalogView catalog(){return new CatalogView(catalog.systemName(),catalog.scene(),catalog.initialStatus(),catalog.partyLabel(),catalog.amountLabel(),catalog.quantityLabel(),catalog.dueLabel(),catalog.modules(),new ArrayList<>(catalog.actions().values()));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Dashboard dashboard(){
        List<BusinessRecord> all=records.findAllByOrderByUpdatedAtDesc(); Map<String,Long> status=new LinkedHashMap<>(),modules=new LinkedHashMap<>();
        all.forEach(item->{status.merge(item.getStatus(),1L,Long::sum);modules.merge(item.getModule(),1L,Long::sum);});
        BigDecimal amount=all.stream().map(BusinessRecord::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        return new Dashboard(all.size(),amount,status,modules,all.stream().limit(6).toList());
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<BusinessRecord> list(String module){return module==null||module.isBlank()?records.findAllByOrderByUpdatedAtDesc():records.findByModuleOrderByUpdatedAtDesc(module);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public BusinessRecord detail(Long id){return get(id);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public PageView search(String module,String status,String riskLevel,String keyword,Boolean overdue,int page,int size){
        if(page<0||size<1||size>200)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"分页参数超出范围");
        List<BusinessRecord> matched=filtered(module,status,riskLevel,keyword,overdue);
        int from=Math.min(page*size,matched.size()),to=Math.min(from+size,matched.size());
        return new PageView(matched.subList(from,to),matched.size(),page,size,(matched.size()+size-1)/size);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public SlaSummary slaSummary(){
        LocalDate today=LocalDate.now();List<BusinessRecord> all=records.findAllByOrderByUpdatedAtDesc();
        long open=all.stream().filter(item->!terminal(item.getStatus())).count();
        long overdue=all.stream().filter(item->!terminal(item.getStatus())&&item.getDueDate()!=null&&item.getDueDate().isBefore(today)).count();
        long dueSoon=all.stream().filter(item->!terminal(item.getStatus())&&item.getDueDate()!=null&&!item.getDueDate().isBefore(today)&&!item.getDueDate().isAfter(today.plusDays(7))).count();
        long highRisk=all.stream().filter(item->"高风险".equals(item.getRiskLevel())).count();
        Map<String,Long> byOwner=new LinkedHashMap<>();all.stream().filter(item->!terminal(item.getStatus())).forEach(item->byOwner.merge(item.getOwner(),1L,Long::sum));
        return new SlaSummary(open,overdue,dueSoon,highRisk,byOwner);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<AuditLog> timeline(Long id){String no=get(id).getRecordNo();return audits.findTop100ByOrderByOccurredAtDesc().stream().filter(log->no.equals(log.getBusinessNo())).toList();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public BusinessRecord comment(Long id,CommentRequest request){BusinessRecord item=get(id);audit(item.getModule(),"协作备注",item.getRecordNo(),request.content());return item;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String exportCsv(String module,String status,String riskLevel,String keyword,Boolean overdue){
        StringBuilder csv=new StringBuilder("业务编号,模块,事项,业务对象,责任人,状态,金额,数量,到期日,风险,说明\n");
        filtered(module,status,riskLevel,keyword,overdue).forEach(item->csv.append(csv(item.getRecordNo())).append(',').append(csv(item.getModule())).append(',').append(csv(item.getTitle())).append(',').append(csv(item.getBusinessParty())).append(',').append(csv(item.getOwner())).append(',').append(csv(item.getStatus())).append(',').append(item.getAmount()).append(',').append(item.getQuantity()).append(',').append(item.getDueDate()==null?"":item.getDueDate()).append(',').append(csv(item.getRiskLevel())).append(',').append(csv(item.getDescription())).append('\n'));
        return "\uFEFF"+csv;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public BusinessRecord create(RecordRequest request){
        requireModule(request.module()); if(records.findByRecordNo(request.recordNo()).isPresent())throw conflict("业务编号已存在");
        BusinessRecord item=records.save(new BusinessRecord(request.recordNo(),request.module(),request.title(),request.businessParty(),request.owner(),catalog.initialStatus(),request.amount(),request.quantity(),request.dueDate(),request.riskLevel(),request.description()));
        audit(request.module(),"创建",request.recordNo(),request.title()); return item;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public BusinessRecord update(Long id,RecordRequest request){
        BusinessRecord item=get(id); if(!item.getStatus().equals(catalog.initialStatus()))throw conflict("只有初始状态记录允许修改"); requireModule(request.module());
        item.update(request.module(),request.title(),request.businessParty(),request.owner(),request.amount(),request.quantity(),request.dueDate(),request.riskLevel(),request.description());
        audit(request.module(),"修改",item.getRecordNo(),request.title()); return item;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public BusinessRecord action(Long id,ActionRequest request){
        BusinessRecord item=get(id); DomainCatalog.WorkflowAction rule=catalog.actions().get(request.action());
        if(rule==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"不支持的流程动作");
        if(!rule.from().contains(item.getStatus()))throw conflict("当前状态“"+item.getStatus()+"”不能执行“"+rule.label()+"”");
        requireRole(rule.requiredRole());
        item.transition(rule.to()); audit(item.getModule(),rule.label(),item.getRecordNo(),request.remark()); return item;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public void delete(Long id){BusinessRecord item=get(id);if(!item.getStatus().equals(catalog.initialStatus()))throw conflict("只有初始状态记录允许删除");records.delete(item);audit(item.getModule(),"删除",item.getRecordNo(),item.getTitle());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<AuditLog> auditLogs(){return audits.findTop100ByOrderByOccurredAtDesc();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Map<String,String> settings(){Map<String,String> result=new LinkedHashMap<>();settings.findAll().stream().sorted(Comparator.comparing(SystemSetting::getSettingKey)).forEach(s->result.put(s.getSettingKey(),s.getSettingValue()));return result;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public Map<String,String> updateSettings(Map<String,String> values){values.forEach((key,value)->{if(value!=null&&!value.isBlank()){SystemSetting setting=settings.findById(key).orElseGet(()->new SystemSetting(key,value));setting.change(value);settings.save(setting);}});audit("SYSTEM","保存设置","SYSTEM",values.keySet().toString());return settings();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private List<BusinessRecord> filtered(String module,String status,String riskLevel,String keyword,Boolean overdue){
        LocalDate today=LocalDate.now();String term=keyword==null?"":keyword.trim().toLowerCase(Locale.ROOT);
        return records.findAllByOrderByUpdatedAtDesc().stream()
            .filter(item->blank(module)||module.equals(item.getModule())).filter(item->blank(status)||status.equals(item.getStatus()))
            .filter(item->blank(riskLevel)||riskLevel.equals(item.getRiskLevel()))
            .filter(item->term.isBlank()||List.of(item.getRecordNo(),item.getTitle(),item.getBusinessParty(),item.getOwner(),item.getDescription()==null?"":item.getDescription()).stream().anyMatch(value->value.toLowerCase(Locale.ROOT).contains(term)))
            .filter(item->overdue==null||!overdue||(!terminal(item.getStatus())&&item.getDueDate()!=null&&item.getDueDate().isBefore(today))).toList();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private boolean terminal(String status){return catalog.actions().values().stream().noneMatch(rule->rule.from().contains(status));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private boolean blank(String value){return value==null||value.isBlank();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String csv(String value){String safe=value==null?"":value;return "\""+safe.replace("\"","\"\"").replace("\r"," ").replace("\n"," ")+"\"";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void requireRole(String role){if(!"ADMIN".equals(role))return;var auth=SecurityContextHolder.getContext().getAuthentication();boolean allowed=auth!=null&&auth.getAuthorities().stream().anyMatch(a->"ROLE_ADMIN".equals(a.getAuthority()));if(!allowed)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"该流程动作需要管理员权限");}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private BusinessRecord get(Long id){return records.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"业务记录不存在"));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void requireModule(String module){if(catalog.modules().stream().noneMatch(item->item.code().equals(module)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"未知业务模块");}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void audit(String module,String action,String no,String detail){var auth=SecurityContextHolder.getContext().getAuthentication();audits.save(new AuditLog(module,action,no,auth==null?"system":auth.getName(),detail==null?"":detail));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Dashboard(long totalRecords,BigDecimal totalAmount,Map<String,Long> statusCounts,Map<String,Long> moduleCounts,List<BusinessRecord> recentRecords){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PageView(List<BusinessRecord> items,long total,int page,int size,int totalPages){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record SlaSummary(long open,long overdue,long dueSoon,long highRisk,Map<String,Long> workloadByOwner){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record CatalogView(String systemName,String scene,String initialStatus,String partyLabel,String amountLabel,String quantityLabel,String dueLabel,List<DomainCatalog.ModuleDefinition> modules,List<DomainCatalog.WorkflowAction> actions){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record RecordRequest(@NotBlank @Size(max=40) String recordNo,@NotBlank String module,@NotBlank @Size(max=120) String title,@NotBlank @Size(max=100) String businessParty,@NotBlank @Size(max=50) String owner,@NotNull @PositiveOrZero BigDecimal amount,@PositiveOrZero int quantity,@NotNull LocalDate dueDate,@NotBlank @Size(max=20) String riskLevel,@Size(max=500) String description){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ActionRequest(@NotBlank String action,@Size(max=300) String remark){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record CommentRequest(@NotBlank @Size(max=500) String content){}
}
