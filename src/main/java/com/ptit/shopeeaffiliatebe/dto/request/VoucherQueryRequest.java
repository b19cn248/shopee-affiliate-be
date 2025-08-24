package com.ptit.shopeeaffiliatebe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.shopeeaffiliatebe.domain.enums.Platform;
import com.ptit.shopeeaffiliatebe.domain.enums.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherQueryRequest {
    
    @JsonProperty("status")
    private VoucherStatus status;
    
    @JsonProperty("platform")
    private Platform platform;
    
    @JsonProperty("q")
    private String searchQuery;
    
    @JsonProperty("active_now")
    private Boolean activeNow;
    
    @JsonProperty("include_deleted")
    private Boolean includeDeleted = false;
}