package com.ptit.shopeeaffiliatebe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.shopeeaffiliatebe.domain.enums.DiscountType;
import com.ptit.shopeeaffiliatebe.domain.enums.Platform;
import com.ptit.shopeeaffiliatebe.domain.enums.VoucherStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUpdateRequest {
    
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @JsonProperty("title")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("platform")
    private Platform platform;
    
    @JsonProperty("discount_type")
    private DiscountType discountType;
    
    @DecimalMin(value = "0", message = "Discount value must be greater than or equal to 0")
    @JsonProperty("discount_value")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0", message = "Minimum order amount must be greater than or equal to 0")
    @JsonProperty("min_order_amount")
    private BigDecimal minOrderAmount;
    
    @JsonProperty("start_at")
    private LocalDateTime startAt;
    
    @JsonProperty("end_at")
    private LocalDateTime endAt;
    
    @PositiveOrZero(message = "Usage limit must be positive or zero")
    @JsonProperty("usage_limit")
    private Integer usageLimit;
    
    @JsonProperty("tags")
    private String[] tags;
    
    @JsonProperty("status")
    private VoucherStatus status;
    
    @Size(max = 500, message = "Product URL must not exceed 500 characters")
    @JsonProperty("product_url")
    private String productUrl;
}