package com.ptit.shopeeaffiliatebe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.shopeeaffiliatebe.domain.enums.DiscountType;
import com.ptit.shopeeaffiliatebe.domain.enums.Platform;
import jakarta.validation.constraints.*;
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
public class VoucherCreateRequest {
    
    @NotBlank(message = "Code is required")
    @Size(max = 64, message = "Code must not exceed 64 characters")
    @JsonProperty("code")
    private String code;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @JsonProperty("title")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @JsonProperty("description")
    private String description;
    
    @NotNull(message = "Platform is required")
    @JsonProperty("platform")
    private Platform platform;
    
    @NotNull(message = "Discount type is required")
    @JsonProperty("discount_type")
    private DiscountType discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0", message = "Discount value must be greater than or equal to 0")
    @JsonProperty("discount_value")
    private BigDecimal discountValue;
    
    @NotNull(message = "Minimum order amount is required")
    @DecimalMin(value = "0", message = "Minimum order amount must be greater than or equal to 0")
    @JsonProperty("min_order_amount")
    private BigDecimal minOrderAmount;
    
    @NotNull(message = "Start date is required")
    @JsonProperty("start_at")
    private LocalDateTime startAt;
    
    @NotNull(message = "End date is required")
    @JsonProperty("end_at")
    private LocalDateTime endAt;
    
    @PositiveOrZero(message = "Usage limit must be positive or zero")
    @JsonProperty("usage_limit")
    private Integer usageLimit;
    
    @JsonProperty("tags")
    private String[] tags;
    
    @Size(max = 500, message = "Product URL must not exceed 500 characters")
    @JsonProperty("product_url")
    private String productUrl;
}