// src/main/java/com/example/Projekat/db/repository/AuditLogRepository.java
package com.example.Projekat.db.repository;

import com.example.Projekat.db.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
