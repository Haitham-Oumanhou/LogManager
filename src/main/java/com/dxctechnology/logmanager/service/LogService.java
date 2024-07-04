package com.dxctechnology.logmanager.service;


import com.dxctechnology.logmanager.model.logEntry;
import com.dxctechnology.logmanager.repository.LogEntryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "C:\\Users\\ASUS\\Desktop\\logs\\dummy_logs.json";

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private  LogEntryRepository logRepository;

    public void readExistingLogs(WebSocketSession session) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
        if (!content.trim().isEmpty()) {
            List<logEntry> logEntries = objectMapper.readValue(content, new TypeReference<List<logEntry>>(){});
            for (logEntry entry : logEntries) {
                incrementAndStore(entry);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(entry)));
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }


    public void readNewLogs(WebSocketSession session) throws Exception {
        final long[] lastKnownSize = {Files.size(Paths.get(LOG_FILE_PATH))};

        executorService.scheduleWithFixedDelay(() -> {
            try {
                long currentSize = Files.size(Paths.get(LOG_FILE_PATH));
                if (currentSize > lastKnownSize[0]) {
                    String content = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));
                    List<logEntry> logEntries = objectMapper.readValue(content, new TypeReference<List<logEntry>>(){});
                    if (!logEntries.isEmpty()) {
                        logEntry lastEntry = logEntries.get(logEntries.size() - 1);
                        incrementAndStore(lastEntry);
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(lastEntry)));
                    }
                    lastKnownSize[0] = currentSize;
                } else {
                    //System.out.println("No new lines");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void incrementAndStore(logEntry logEntry) {
        logEntry latestLogEntry = logRepository.findLatestFrequencyByServiceAndMethodName(logEntry.getService(), logEntry.getMethod_name());
        if (latestLogEntry == null) {
            logEntry.setFrequency(1);
        } else {
            int newFrequency = latestLogEntry.getFrequency() + 1;
            logEntry.setFrequency(newFrequency);
        }
        logRepository.save(logEntry);
    }


    public List<logEntry> getLogs() {
        return logRepository.findAll();
    }


    public List <logEntry> getLogsByLabel(String label) {
        return logRepository.findByLabel(label);
    }
    
    public List <logEntry> getLogsByServiceAndMethodName(String service, String method_name) {
        return logRepository.findRecentFrequencyByServiceAndMethodName(service, method_name);
    }
}



