/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.time.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected EnterpriseControl(){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseControl(String controlNo,String organizationCode,String fiscalPeriod,String controlType,
            String subjectNo,String subjectName,String assignee,String riskLevel,LocalDate dueDate,
            String externalSystem,String externalRef,String idempotencyKey){
        this.controlNo=controlNo;this.organizationCode=organizationCode;this.fiscalPeriod=fiscalPeriod;
        this.controlType=controlType;this.subjectNo=subjectNo;this.subjectName=subjectName;this.assignee=assignee;
        this.state="DRAFT";this.riskLevel=riskLevel;this.dueDate=dueDate;this.externalSystem=externalSystem;
        this.externalRef=externalRef;this.idempotencyKey=idempotencyKey;this.syncState="NOT_QUEUED";
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PrePersist void created(){createdAt=updatedAt=LocalDateTime.now();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PreUpdate void updated(){updatedAt=LocalDateTime.now();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void submit(){state="PENDING_REVIEW";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void approve(){state="APPROVED";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void reject(){state="REJECTED";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void complete(){state="COMPLETED";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void addDocument(){documentCount++;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void sync(String state,String reference){syncState=state;if(reference!=null&&!reference.isBlank())externalRef=reference;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId(){return id;} /**
                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                     */
public String getControlNo(){return controlNo;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getOrganizationCode(){return organizationCode;} /**
                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                   */
public String getFiscalPeriod(){return fiscalPeriod;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getControlType(){return controlType;} /**
                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                         */
public String getSubjectNo(){return subjectNo;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getSubjectName(){return subjectName;} /**
                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                         */
public String getAssignee(){return assignee;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getState(){return state;} /**
                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                             */
public String getRiskLevel(){return riskLevel;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDate getDueDate(){return dueDate;} /**
                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                    */
public String getExternalSystem(){return externalSystem;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getExternalRef(){return externalRef;} /**
                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                         */
public String getIdempotencyKey(){return idempotencyKey;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getSyncState(){return syncState;} /**
                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                     */
public int getDocumentCount(){return documentCount;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public long getVersion(){return version;} /**
                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                               */
public LocalDateTime getCreatedAt(){return createdAt;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getUpdatedAt(){return updatedAt;}
}
