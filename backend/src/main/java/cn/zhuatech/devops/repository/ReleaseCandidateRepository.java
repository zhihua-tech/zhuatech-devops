/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.repository;

import cn.zhuatech.devops.model.ReleaseCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReleaseCandidateRepository extends JpaRepository<ReleaseCandidate,Long> {
    Optional<ReleaseCandidate> findByReleaseNo(String releaseNo);
    List<ReleaseCandidate> findAllByOrderByUpdatedAtDesc();
    long countByState(String state);
}
