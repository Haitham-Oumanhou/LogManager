package com.dxctechnology.logmanager.controller;

import com.dxctechnology.logmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Component
public class LogWebSocketHandler extends TextWebSocketHandler {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public LogService logService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logService.readExistingLogs(session);
        logService.readNewLogs(session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        executorService.shutdown();
    }






}