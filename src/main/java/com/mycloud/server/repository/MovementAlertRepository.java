package com.mycloud.server.repository;

import com.mycloud.server.model.MovementAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementAlertRepository extends JpaRepository<MovementAlert, Long> {
    List<MovementAlert> findByTimestampAfterOrderByTimestampDesc(LocalDateTime after);
}
