/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=30) private String module;
    @Column(nullable=false,length=40) private String action;
    @Column(nullable=false,length=50) private String businessNo;
    @Column(nullable=false,length=50) private String operatorName;
    @Column(length=500) private String detail;
    private LocalDateTime occurredAt;
    protected AuditLog() {}
    public AuditLog(String module,String action,String businessNo,String operatorName,String detail){
        this.module=module;this.action=action;this.businessNo=businessNo;this.operatorName=operatorName;this.detail=detail;this.occurredAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getModule(){return module;} public String getAction(){return action;}
    public String getBusinessNo(){return businessNo;} public String getOperatorName(){return operatorName;}
    public String getDetail(){return detail;} public LocalDateTime getOccurredAt(){return occurredAt;}
}
