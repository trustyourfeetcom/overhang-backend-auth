package com.trustyourfeet.overhang.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static ResponseEntity<Object> generateResponse(
            HttpStatus status,
            String message,
            Object responseObj,
            String error) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        map.put("message", message);
        map.put("data", responseObj);
        map.put("error", error);

        return ResponseEntity.status(status).body(map);
    }
}
