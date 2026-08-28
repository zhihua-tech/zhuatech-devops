/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="control_documents")
public class ControlDocument {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long controlId;
    @Column(nullable=false,length=160) private String fileName;
    @Column(nullable=false,length=100) private String mediaType;
    @Column(nullable=false) private long sizeBytes;
    @Column(nullable=false,length=64) private String sha256;
    @Column(nullable=false,length=120) private String storageKey;
    @Column(nullable=false,length=50) private String uploadedBy;
    private LocalDateTime createdAt;
    protected ControlDocument(){}
    public ControlDocument(Long controlId,String fileName,String mediaType,long sizeBytes,String sha256,String storageKey,String uploadedBy){
        this.controlId=controlId;this.fileName=fileName;this.mediaType=mediaType;this.sizeBytes=sizeBytes;
        this.sha256=sha256;this.storageKey=storageKey;this.uploadedBy=uploadedBy;this.createdAt=LocalDateTime.now();
    }
    public Long getId(){return id;} public Long getControlId(){return controlId;} public String getFileName(){return fileName;}
    public String getMediaType(){return mediaType;} public long getSizeBytes(){return sizeBytes;} public String getSha256(){return sha256;}
    public String getStorageKey(){return storageKey;} public String getUploadedBy(){return uploadedBy;} public LocalDateTime getCreatedAt(){return createdAt;}
}
