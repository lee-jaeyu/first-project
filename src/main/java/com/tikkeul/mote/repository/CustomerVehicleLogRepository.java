package com.tikkeul.mote.repository;

import com.tikkeul.mote.entity.CustomerVehicleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerVehicleLogRepository extends JpaRepository<CustomerVehicleLog, Long> {
    List<CustomerVehicleLog> findByPlate(String plate);
}
