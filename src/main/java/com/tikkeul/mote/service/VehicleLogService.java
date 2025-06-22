package com.tikkeul.mote.service;

import com.tikkeul.mote.entity.CustomerVehicleLog;
import com.tikkeul.mote.repository.CustomerVehicleLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class VehicleLogService {

    @Autowired
    private CustomerVehicleLogRepository repository;

    public void saveLog(Map<String, Object> gps, Map<String, Object> ocr) {
        CustomerVehicleLog log = new CustomerVehicleLog();
        log.setPlate((String) ocr.get("plate"));
        log.setRaw((String) ocr.get("raw"));
        log.setConfidence(((Number) ocr.get("confidence")).doubleValue());
        log.setLatitude(((Number) gps.get("latitude")).doubleValue());
        log.setLongitude(((Number) gps.get("longitude")).doubleValue());
        repository.save(log);
    }
}