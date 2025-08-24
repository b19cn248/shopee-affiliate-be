package com.ptit.shopeeaffiliatebe.mapper;

import com.ptit.shopeeaffiliatebe.domain.entity.Voucher;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherCreateRequest;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherUpdateRequest;
import com.ptit.shopeeaffiliatebe.dto.response.VoucherResponse;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {
    
    public Voucher toEntity(VoucherCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setTitle(request.getTitle());
        voucher.setDescription(request.getDescription());
        voucher.setPlatform(request.getPlatform());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setStartAt(request.getStartAt());
        voucher.setEndAt(request.getEndAt());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setTags(request.getTags());
        voucher.setProductUrl(request.getProductUrl());
        
        return voucher;
    }
    
    public void updateEntity(Voucher voucher, VoucherUpdateRequest request) {
        if (request == null || voucher == null) {
            return;
        }
        
        if (request.getTitle() != null) {
            voucher.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            voucher.setDescription(request.getDescription());
        }
        if (request.getPlatform() != null) {
            voucher.setPlatform(request.getPlatform());
        }
        if (request.getDiscountType() != null) {
            voucher.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            voucher.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinOrderAmount() != null) {
            voucher.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getStartAt() != null) {
            voucher.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            voucher.setEndAt(request.getEndAt());
        }
        if (request.getUsageLimit() != null) {
            voucher.setUsageLimit(request.getUsageLimit());
        }
        if (request.getTags() != null) {
            voucher.setTags(request.getTags());
        }
        if (request.getStatus() != null) {
            voucher.setStatus(request.getStatus());
        }
        if (request.getProductUrl() != null) {
            voucher.setProductUrl(request.getProductUrl());
        }
    }
    
    public VoucherResponse toResponse(Voucher voucher) {
        if (voucher == null) {
            return null;
        }
        
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .platform(voucher.getPlatform() != null ? voucher.getPlatform().name() : null)
                .discountType(voucher.getDiscountType() != null ? voucher.getDiscountType().name() : null)
                .discountValue(voucher.getDiscountValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .startAt(voucher.getStartAt())
                .endAt(voucher.getEndAt())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .status(voucher.getStatus() != null ? voucher.getStatus().name() : null)
                .tags(voucher.getTags())
                .productUrl(voucher.getProductUrl())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .createdBy(voucher.getCreatedBy())
                .updatedBy(voucher.getUpdatedBy())
                .isActive(voucher.isActive())
                .isExpired(voucher.isExpired())
                .build();
    }
}