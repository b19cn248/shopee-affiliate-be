package com.ptit.shopeeaffiliatebe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("details")
    private Map<String, String> details;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("trace_id")
    private String traceId;
}