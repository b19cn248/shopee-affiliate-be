package com.ptit.shopeeaffiliatebe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("page_number")
    private int pageNumber;
    
    @JsonProperty("page_size")
    private int pageSize;
    
    @JsonProperty("total_elements")
    private long totalElements;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    @JsonProperty("last")
    private boolean last;
    
    @JsonProperty("first")
    private boolean first;
}