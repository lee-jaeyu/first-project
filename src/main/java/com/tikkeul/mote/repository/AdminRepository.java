package com.tikkeul.mote.repository;

import com.tikkeul.mote.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserName(String userName);
    Optional<Admin> findByBusinessNo(String businessNo);

    boolean existsByUserName(String userName);
    boolean existsByBusinessNo(String businessNo);
}
