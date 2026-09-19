/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected ControlDocument(){}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ControlDocument(Long controlId,String fileName,String mediaType,long sizeBytes,String sha256,String storageKey,String uploadedBy){
        this.controlId=controlId;this.fileName=fileName;this.mediaType=mediaType;this.sizeBytes=sizeBytes;
        this.sha256=sha256;this.storageKey=storageKey;this.uploadedBy=uploadedBy;this.createdAt=LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId(){return id;} /**
                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                     */
public Long getControlId(){return controlId;} /**
                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                   */
public String getFileName(){return fileName;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getMediaType(){return mediaType;} /**
                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                     */
public long getSizeBytes(){return sizeBytes;} /**
                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                   */
public String getSha256(){return sha256;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStorageKey(){return storageKey;} /**
                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                       */
public String getUploadedBy(){return uploadedBy;} /**
                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                         */
public LocalDateTime getCreatedAt(){return createdAt;}
}
