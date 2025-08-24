package com.ptit.shopeeaffiliatebe.service;

import com.ptit.shopeeaffiliatebe.domain.entity.Voucher;
import com.ptit.shopeeaffiliatebe.domain.enums.VoucherStatus;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherCreateRequest;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherQueryRequest;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherUpdateRequest;
import com.ptit.shopeeaffiliatebe.dto.response.PageResponse;
import com.ptit.shopeeaffiliatebe.dto.response.VoucherResponse;
import com.ptit.shopeeaffiliatebe.exception.ConflictException;
import com.ptit.shopeeaffiliatebe.exception.NotFoundException;
import com.ptit.shopeeaffiliatebe.mapper.VoucherMapper;
import com.ptit.shopeeaffiliatebe.repository.VoucherRepository;
import com.ptit.shopeeaffiliatebe.repository.specification.VoucherSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoucherService {
    
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    
    @Transactional
    public VoucherResponse createVoucher(VoucherCreateRequest request, String username) {
        log.info("Creating voucher with code: {}", request.getCode());
        
        // Check if voucher code already exists
        if (voucherRepository.existsByCodeAndDeletedAtIsNull(request.getCode())) {
            throw new ConflictException("Voucher code already exists: " + request.getCode());
        }
        
        // Validate date range
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Map to entity
        Voucher voucher = voucherMapper.toEntity(request);
        
        // Set default values
        voucher.setStatus(VoucherStatus.DRAFT);
        voucher.setCreatedBy(username != null ? username : "system");
        voucher.setUpdatedBy(username != null ? username : "system");
        
        // Save and return
        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Voucher created successfully with id: {}", savedVoucher.getId());
        
        return voucherMapper.toResponse(savedVoucher);
    }
    
    @Transactional
    public VoucherResponse updateVoucher(Long id, VoucherUpdateRequest request, String username) {
        log.info("Updating voucher with id: {}", id);
        
        // Find voucher
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + id));
        
        // Validate date range if provided
        LocalDateTime startAt = request.getStartAt() != null ? request.getStartAt() : voucher.getStartAt();
        LocalDateTime endAt = request.getEndAt() != null ? request.getEndAt() : voucher.getEndAt();
        if (endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Update entity
        voucherMapper.updateEntity(voucher, request);
        voucher.setUpdatedBy(username != null ? username : "system");
        
        // Save and return
        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Voucher updated successfully with id: {}", id);
        
        return voucherMapper.toResponse(updatedVoucher);
    }
    
    @Transactional
    public void deleteVoucher(Long id, String username) {
        log.info("Soft deleting voucher with id: {}", id);
        
        // Find voucher
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + id));
        
        // Soft delete
        voucher.setDeletedAt(LocalDateTime.now());
        voucher.setUpdatedBy(username != null ? username : "system");
        
        voucherRepository.save(voucher);
        log.info("Voucher soft deleted successfully with id: {}", id);
    }
    
    @Transactional
    public void restoreVoucher(Long id, String username) {
        log.info("Restoring voucher with id: {}", id);
        
        // Find voucher including deleted
        Voucher voucher = voucherRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + id));
        
        if (voucher.getDeletedAt() == null) {
            throw new IllegalArgumentException("Voucher is not deleted");
        }
        
        // Restore
        voucher.setDeletedAt(null);
        voucher.setUpdatedBy(username != null ? username : "system");
        
        voucherRepository.save(voucher);
        log.info("Voucher restored successfully with id: {}", id);
    }
    
    public VoucherResponse getVoucherById(Long id) {
        log.debug("Getting voucher by id: {}", id);
        
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + id));
        
        return voucherMapper.toResponse(voucher);
    }
    
    public VoucherResponse getVoucherByCode(String code) {
        log.debug("Getting voucher by code: {}", code);
        
        Voucher voucher = voucherRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new NotFoundException("Voucher not found with code: " + code));
        
        return voucherMapper.toResponse(voucher);
    }
    
    public PageResponse<VoucherResponse> searchVouchers(VoucherQueryRequest query, Pageable pageable) {
        log.debug("Searching vouchers with query: {}", query);
        
        // Build specification
        Specification<Voucher> spec = VoucherSpecification.buildSpecification(query);
        
        // Execute query
        Page<Voucher> voucherPage = voucherRepository.findAll(spec, pageable);
        
        // Map to response
        List<VoucherResponse> voucherResponses = voucherPage.getContent().stream()
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<VoucherResponse>builder()
                .content(voucherResponses)
                .pageNumber(voucherPage.getNumber())
                .pageSize(voucherPage.getSize())
                .totalElements(voucherPage.getTotalElements())
                .totalPages(voucherPage.getTotalPages())
                .last(voucherPage.isLast())
                .first(voucherPage.isFirst())
                .build();
    }
    
    @Transactional
    public VoucherResponse incrementUsedCount(Long id) {
        log.info("Incrementing used count for voucher id: {}", id);
        
        Voucher voucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + id));
        
        // Check if voucher can be used
        if (!voucher.isActive()) {
            throw new IllegalArgumentException("Voucher is not active");
        }
        
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new IllegalArgumentException("Voucher usage limit reached");
        }
        
        // Increment used count
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        
        // Check if should be deactivated
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            voucher.setStatus(VoucherStatus.INACTIVE);
        }
        
        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Used count incremented for voucher id: {}", id);
        
        return voucherMapper.toResponse(updatedVoucher);
    }
    
    @Transactional
    public void updateExpiredVouchers() {
        log.info("Updating expired vouchers");
        voucherRepository.updateExpiredVouchers(LocalDateTime.now());
    }
}