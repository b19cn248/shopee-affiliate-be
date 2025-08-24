package com.ptit.shopeeaffiliatebe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("discount_type")
    private String discountType;
    
    @JsonProperty("discount_value")
    private BigDecimal discountValue;
    
    @JsonProperty("min_order_amount")
    private BigDecimal minOrderAmount;
    
    @JsonProperty("start_at")
    private LocalDateTime startAt;
    
    @JsonProperty("end_at")
    private LocalDateTime endAt;
    
    @JsonProperty("usage_limit")
    private Integer usageLimit;
    
    @JsonProperty("used_count")
    private Integer usedCount;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("tags")
    private String[] tags;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("created_by")
    private String createdBy;
    
    @JsonProperty("updated_by")
    private String updatedBy;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("is_expired")
    private Boolean isExpired;
}