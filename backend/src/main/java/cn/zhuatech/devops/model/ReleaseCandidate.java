/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    protected ReleaseCandidate(){}
    public ReleaseCandidate(String releaseNo,String applicationCode,String commitSha,String artifactDigest,
            String environment,double testPassRate,int criticalVulnerabilities,String rollbackVersion,
            String changeTicket,LocalDateTime scheduledAt,boolean emergencyApproval){
        this.releaseNo=releaseNo;this.applicationCode=applicationCode;this.commitSha=commitSha;
        this.artifactDigest=artifactDigest;this.environment=environment;this.testPassRate=testPassRate;
        this.criticalVulnerabilities=criticalVulnerabilities;this.rollbackVersion=rollbackVersion;
        this.changeTicket=changeTicket;this.scheduledAt=scheduledAt;this.emergencyApproval=emergencyApproval;this.state="DRAFT";
    }
    @PrePersist void created(){createdAt=updatedAt=LocalDateTime.now();}
    @PreUpdate void updated(){updatedAt=LocalDateTime.now();}
    public void submit(){state="PENDING_APPROVAL";} public void approve(){state="APPROVED";}
    public void deploy(){state="DEPLOYED";} public void rollback(){state="ROLLED_BACK";}

    public Long getId(){return id;} public String getReleaseNo(){return releaseNo;}
    public String getApplicationCode(){return applicationCode;} public String getCommitSha(){return commitSha;}
    public String getArtifactDigest(){return artifactDigest;} public String getEnvironment(){return environment;}
    public double getTestPassRate(){return testPassRate;} public int getCriticalVulnerabilities(){return criticalVulnerabilities;}
    public String getState(){return state;} public String getRollbackVersion(){return rollbackVersion;}
    public String getChangeTicket(){return changeTicket;} public LocalDateTime getScheduledAt(){return scheduledAt;}
    public boolean isEmergencyApproval(){return emergencyApproval;}
    public long getVersion(){return version;} public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
}
