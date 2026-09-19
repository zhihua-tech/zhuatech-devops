/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name="release_candidates",uniqueConstraints=@UniqueConstraint(columnNames="releaseNo"))
public class ReleaseCandidate {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=40) private String releaseNo;
    @Column(nullable=false,length=60) private String applicationCode;
    @Column(nullable=false,length=40) private String commitSha;
    @Column(nullable=false,length=71) private String artifactDigest;
    @Column(nullable=false,length=20) private String environment;
    private double testPassRate;
    private int criticalVulnerabilities;
    @Column(nullable=false,length=30) private String state;
    @Column(nullable=false,length=60) private String rollbackVersion;
    @Column(nullable=false,length=50) private String changeTicket;
    @Column(nullable=false) private LocalDateTime scheduledAt;
    private boolean emergencyApproval;
    @Version private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected ReleaseCandidate(){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ReleaseCandidate(String releaseNo,String applicationCode,String commitSha,String artifactDigest,
            String environment,double testPassRate,int criticalVulnerabilities,String rollbackVersion,
            String changeTicket,LocalDateTime scheduledAt,boolean emergencyApproval){
        this.releaseNo=releaseNo;this.applicationCode=applicationCode;this.commitSha=commitSha;
        this.artifactDigest=artifactDigest;this.environment=environment;this.testPassRate=testPassRate;
        this.criticalVulnerabilities=criticalVulnerabilities;this.rollbackVersion=rollbackVersion;
        this.changeTicket=changeTicket;this.scheduledAt=scheduledAt;this.emergencyApproval=emergencyApproval;this.state="DRAFT";
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
    public void submit(){state="PENDING_APPROVAL";} /**
                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                     */
public void approve(){state="APPROVED";}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void deploy(){state="DEPLOYED";} /**
                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                             */
public void rollback(){state="ROLLED_BACK";}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId(){return id;} /**
                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                     */
public String getReleaseNo(){return releaseNo;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getApplicationCode(){return applicationCode;} /**
                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                 */
public String getCommitSha(){return commitSha;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getArtifactDigest(){return artifactDigest;} /**
                                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                               */
public String getEnvironment(){return environment;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public double getTestPassRate(){return testPassRate;} /**
                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                           */
public int getCriticalVulnerabilities(){return criticalVulnerabilities;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getState(){return state;} /**
                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                             */
public String getRollbackVersion(){return rollbackVersion;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getChangeTicket(){return changeTicket;} /**
                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                           */
public LocalDateTime getScheduledAt(){return scheduledAt;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isEmergencyApproval(){return emergencyApproval;}
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
