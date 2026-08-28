/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="enterprise_controls", uniqueConstraints={
    @UniqueConstraint(columnNames="controlNo"),
    @UniqueConstraint(columnNames="idempotencyKey")
})
public class EnterpriseControl {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=40) private String controlNo;
    @Column(nullable=false,length=40) private String organizationCode;
    @Column(nullable=false,length=7) private String fiscalPeriod;
    @Column(nullable=false,length=40) private String controlType;
    @Column(nullable=false,length=60) private String subjectNo;
    @Column(nullable=false,length=120) private String subjectName;
    @Column(nullable=false,length=50) private String assignee;
    @Column(nullable=false,length=24) private String state;
    @Column(nullable=false,length=20) private String riskLevel;
    @Column(nullable=false) private LocalDate dueDate;
    @Column(length=40) private String externalSystem;
    @Column(length=100) private String externalRef;
    @Column(nullable=false,length=80) private String idempotencyKey;
    @Column(nullable=false,length=20) private String syncState;
    @Column(nullable=false) private int documentCount;
    @Version private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    protected EnterpriseControl(){}
    public EnterpriseControl(String controlNo,String organizationCode,String fiscalPeriod,String controlType,
            String subjectNo,String subjectName,String assignee,String riskLevel,LocalDate dueDate,
            String externalSystem,String externalRef,String idempotencyKey){
        this.controlNo=controlNo;this.organizationCode=organizationCode;this.fiscalPeriod=fiscalPeriod;
        this.controlType=controlType;this.subjectNo=subjectNo;this.subjectName=subjectName;this.assignee=assignee;
        this.state="DRAFT";this.riskLevel=riskLevel;this.dueDate=dueDate;this.externalSystem=externalSystem;
        this.externalRef=externalRef;this.idempotencyKey=idempotencyKey;this.syncState="NOT_QUEUED";
    }
    @PrePersist void created(){createdAt=updatedAt=LocalDateTime.now();}
    @PreUpdate void updated(){updatedAt=LocalDateTime.now();}
    public void submit(){state="PENDING_REVIEW";}
    public void approve(){state="APPROVED";}
    public void reject(){state="REJECTED";}
    public void complete(){state="COMPLETED";}
    public void addDocument(){documentCount++;}
    public void sync(String state,String reference){syncState=state;if(reference!=null&&!reference.isBlank())externalRef=reference;}
    public Long getId(){return id;} public String getControlNo(){return controlNo;}
    public String getOrganizationCode(){return organizationCode;} public String getFiscalPeriod(){return fiscalPeriod;}
    public String getControlType(){return controlType;} public String getSubjectNo(){return subjectNo;}
    public String getSubjectName(){return subjectName;} public String getAssignee(){return assignee;}
    public String getState(){return state;} public String getRiskLevel(){return riskLevel;}
    public LocalDate getDueDate(){return dueDate;} public String getExternalSystem(){return externalSystem;}
    public String getExternalRef(){return externalRef;} public String getIdempotencyKey(){return idempotencyKey;}
    public String getSyncState(){return syncState;} public int getDocumentCount(){return documentCount;}
    public long getVersion(){return version;} public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
}
