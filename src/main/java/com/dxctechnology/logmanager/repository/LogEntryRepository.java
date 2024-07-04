package com.dxctechnology.logmanager.repository;

import com.dxctechnology.logmanager.model.logEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LogEntryRepository extends MongoRepository <logEntry, Long> {
    @Query(value = "{ 'service' : ?0, 'method_name' : ?1 }", sort = "{ 'frequency' : -1 }")
    List<logEntry> findRecentFrequencyByServiceAndMethodName(String service, String method_name);



    default logEntry findLatestFrequencyByServiceAndMethodName(String service, String method_name) {
        List<logEntry> entries = findRecentFrequencyByServiceAndMethodName(service, method_name);
        return entries.isEmpty() ? null : entries.get(0);
    }

    List<logEntry> findByLabel(String label);
}

