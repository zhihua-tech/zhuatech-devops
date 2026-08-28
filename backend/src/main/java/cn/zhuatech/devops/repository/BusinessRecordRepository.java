/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.repository;
import cn.zhuatech.devops.model.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long>{
    List<BusinessRecord> findAllByOrderByUpdatedAtDesc();
    List<BusinessRecord> findByModuleOrderByUpdatedAtDesc(String module);
    Optional<BusinessRecord> findByRecordNo(String recordNo);
    long countByStatus(String status);
}
