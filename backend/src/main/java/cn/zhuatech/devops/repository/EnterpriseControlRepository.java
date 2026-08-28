/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.repository;
import cn.zhuatech.devops.model.EnterpriseControl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EnterpriseControlRepository extends JpaRepository<EnterpriseControl,Long>{
    Optional<EnterpriseControl> findByControlNo(String controlNo);
    Optional<EnterpriseControl> findByIdempotencyKey(String idempotencyKey);
    List<EnterpriseControl> findAllByOrderByUpdatedAtDesc();
    List<EnterpriseControl> findByStateOrderByUpdatedAtDesc(String state);
}
