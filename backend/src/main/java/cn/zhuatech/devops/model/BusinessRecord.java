/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
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
    protected BusinessRecord() {}
    public BusinessRecord(String recordNo, String module, String title, String businessParty, String owner,
            String status, BigDecimal amount, int quantity, LocalDate dueDate, String riskLevel, String description) {
        this.recordNo=recordNo; this.module=module; this.title=title; this.businessParty=businessParty;
        this.owner=owner; this.status=status; this.amount=amount; this.quantity=quantity; this.dueDate=dueDate;
        this.riskLevel=riskLevel; this.description=description;
    }
    @PrePersist void createTime(){createdAt=updatedAt=LocalDateTime.now();}
    @PreUpdate void updateTime(){updatedAt=LocalDateTime.now();}
    public void update(String module,String title,String party,String owner,BigDecimal amount,int quantity,LocalDate dueDate,String risk,String description){
        this.module=module; this.title=title; this.businessParty=party; this.owner=owner; this.amount=amount;
        this.quantity=quantity; this.dueDate=dueDate; this.riskLevel=risk; this.description=description;
    }
    public void transition(String status){this.status=status;}
    public Long getId(){return id;} public String getRecordNo(){return recordNo;} public String getModule(){return module;}
    public String getTitle(){return title;} public String getBusinessParty(){return businessParty;} public String getOwner(){return owner;}
    public String getStatus(){return status;} public BigDecimal getAmount(){return amount;} public int getQuantity(){return quantity;}
    public LocalDate getDueDate(){return dueDate;} public String getRiskLevel(){return riskLevel;} public String getDescription(){return description;}
    public long getVersion(){return version;} public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
}
