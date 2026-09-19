/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.repository;
import cn.zhuatech.devops.model.EnterpriseControl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface EnterpriseControlRepository extends JpaRepository<EnterpriseControl,Long>{
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<EnterpriseControl> findByControlNo(String controlNo);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<EnterpriseControl> findByIdempotencyKey(String idempotencyKey);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<EnterpriseControl> findAllByOrderByUpdatedAtDesc();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<EnterpriseControl> findByStateOrderByUpdatedAtDesc(String state);
}
