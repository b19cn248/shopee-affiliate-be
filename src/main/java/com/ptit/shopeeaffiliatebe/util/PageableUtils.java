package com.ptit.shopeeaffiliatebe.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

public class PageableUtils {
    
    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();
    
    static {
        FIELD_MAPPING.put("created_at", "createdAt");
        FIELD_MAPPING.put("updated_at", "updatedAt");
        FIELD_MAPPING.put("start_at", "startAt");
        FIELD_MAPPING.put("end_at", "endAt");
        FIELD_MAPPING.put("discount_type", "discountType");
        FIELD_MAPPING.put("discount_value", "discountValue");
        FIELD_MAPPING.put("min_order_amount", "minOrderAmount");
        FIELD_MAPPING.put("usage_limit", "usageLimit");
        FIELD_MAPPING.put("used_count", "usedCount");
        FIELD_MAPPING.put("created_by", "createdBy");
        FIELD_MAPPING.put("updated_by", "updatedBy");
        FIELD_MAPPING.put("deleted_at", "deletedAt");
    }
    
    public static Pageable mapFieldNames(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }
        
        Sort.Order[] orders = pageable.getSort().stream()
                .map(PageableUtils::mapSortOrder)
                .toArray(Sort.Order[]::new);
        
        Sort mappedSort = Sort.by(orders);
        
        return PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                mappedSort
        );
    }
    
    private static Sort.Order mapSortOrder(Sort.Order order) {
        String originalProperty = order.getProperty();
        String mappedProperty = FIELD_MAPPING.getOrDefault(originalProperty, originalProperty);
        
        return new Sort.Order(order.getDirection(), mappedProperty);
    }
}