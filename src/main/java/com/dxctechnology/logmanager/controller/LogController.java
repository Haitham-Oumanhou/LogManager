package com.dxctechnology.logmanager.controller;

import com.dxctechnology.logmanager.model.logEntry;
import com.dxctechnology.logmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")


public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/logs")
    public List<logEntry> getLogs(@RequestParam(required = false) String label) {
        if (label != null) {
            return logService.getLogsByLabel(label);
        } else {
            return logService.getLogs();
        }
    }

    @GetMapping("/logs/{service}/{method_name}")
    public List<logEntry> getLogByServiceAndMethodName(@PathVariable String service, @PathVariable String method_name) {
        return logService.getLogsByServiceAndMethodName(service, method_name);
    }



}
