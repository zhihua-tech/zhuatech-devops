/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="business_records", uniqueConstraints=@UniqueConstraint(columnNames="recordNo"))
public class BusinessRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Version private long version;
    @Column(nullable=false, length=40) private String recordNo;
    @Column(nullable=false, length=30) private String module;
    @Column(nullable=false, length=120) private String title;
    @Column(nullable=false, length=100) private String businessParty;
    @Column(nullable=false, length=50) private String owner;
    @Column(nullable=false, length=30) private String status;
    @Column(nullable=false, precision=18, scale=2) private BigDecimal amount;
    private int quantity;
    private LocalDate dueDate;
    @Column(nullable=false, length=20) private String riskLevel;
    @Column(length=500) private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected BusinessRecord() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public BusinessRecord(String recordNo, String module, String title, String businessParty, String owner,
            String status, BigDecimal amount, int quantity, LocalDate dueDate, String riskLevel, String description) {
        this.recordNo=recordNo; this.module=module; this.title=title; this.businessParty=businessParty;
        this.owner=owner; this.status=status; this.amount=amount; this.quantity=quantity; this.dueDate=dueDate;
        this.riskLevel=riskLevel; this.description=description;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PrePersist void createTime(){createdAt=updatedAt=LocalDateTime.now();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PreUpdate void updateTime(){updatedAt=LocalDateTime.now();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void update(String module,String title,String party,String owner,BigDecimal amount,int quantity,LocalDate dueDate,String risk,String description){
        this.module=module; this.title=title; this.businessParty=party; this.owner=owner; this.amount=amount;
        this.quantity=quantity; this.dueDate=dueDate; this.riskLevel=risk; this.description=description;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void transition(String status){this.status=status;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId(){return id;} /**
                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                     */
public String getRecordNo(){return recordNo;} /**
                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                   */
public String getModule(){return module;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getTitle(){return title;} /**
                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                             */
public String getBusinessParty(){return businessParty;} /**
                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                     */
public String getOwner(){return owner;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus(){return status;} /**
                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                               */
public BigDecimal getAmount(){return amount;} /**
                                                                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                             */
public int getQuantity(){return quantity;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDate getDueDate(){return dueDate;} /**
                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                    */
public String getRiskLevel(){return riskLevel;} /**
                                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                    */
public String getDescription(){return description;}
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
