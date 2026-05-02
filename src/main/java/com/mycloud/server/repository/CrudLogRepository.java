package com.mycloud.server.repository;

import com.mycloud.server.model.CrudLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrudLogRepository extends JpaRepository<CrudLog,Long> {
    List<CrudLog> findByUserIdOrderByTimestampDesc(Long id);
    List<CrudLog> findByFileIdOrderByTimestampDesc(Long fileId);
}
