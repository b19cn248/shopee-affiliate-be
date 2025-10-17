package com.ptit.shopeeaffiliatebe.controller;

import com.ptit.shopeeaffiliatebe.dto.request.VoucherCreateRequest;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherQueryRequest;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherUpdateRequest;
import com.ptit.shopeeaffiliatebe.dto.response.PageResponse;
import com.ptit.shopeeaffiliatebe.dto.response.VoucherResponse;
import com.ptit.shopeeaffiliatebe.service.VoucherService;
import com.ptit.shopeeaffiliatebe.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vouchers")
@RequiredArgsConstructor
@Slf4j
public class VoucherController {
    
    private final VoucherService voucherService;
    
    @GetMapping
    @Operation(summary = "Search vouchers", description = "Search vouchers with filters and pagination")
    public ResponseEntity<PageResponse<VoucherResponse>> searchVouchers(
            @Parameter(description = "Query parameters")
            VoucherQueryRequest query,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        log.info("Searching vouchers with query: {}", query);
        
        // Map snake_case field names to camelCase for JPA
        Pageable mappedPageable = PageableUtils.mapFieldNames(pageable);
        
        PageResponse<VoucherResponse> response = voucherService.searchVouchers(query, mappedPageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get voucher by ID", description = "Get a single voucher by its ID")
    public ResponseEntity<VoucherResponse> getVoucherById(
            @Parameter(description = "Voucher ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting voucher by id: {}", id);
        VoucherResponse response = voucherService.getVoucherById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get voucher by code", description = "Get a single voucher by its code")
    public ResponseEntity<VoucherResponse> getVoucherByCode(
            @Parameter(description = "Voucher code", required = true)
            @PathVariable String code) {
        
        log.info("Getting voucher by code: {}", code);
        VoucherResponse response = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create voucher", description = "Create a new voucher")
    public ResponseEntity<VoucherResponse> createVoucher(
            @Valid @RequestBody VoucherCreateRequest request,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        log.info("Creating voucher with code: {}", request.getCode());
        VoucherResponse response = voucherService.createVoucher(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update voucher", description = "Update an existing voucher")
    public ResponseEntity<VoucherResponse> updateVoucher(
            @Parameter(description = "Voucher ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody VoucherUpdateRequest request,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        log.info("Updating voucher with id: {}", id);
        VoucherResponse response = voucherService.updateVoucher(id, request, username);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete voucher", description = "Soft delete a voucher")
    public ResponseEntity<Void> deleteVoucher(
            @Parameter(description = "Voucher ID", required = true)
            @PathVariable Long id,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        log.info("Deleting voucher with id: {}", id);
        voucherService.deleteVoucher(id, username);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore voucher", description = "Restore a soft deleted voucher")
    public ResponseEntity<Void> restoreVoucher(
            @Parameter(description = "Voucher ID", required = true)
            @PathVariable Long id,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        log.info("Restoring voucher with id: {}", id);
        voucherService.restoreVoucher(id, username);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/use")
    @Operation(summary = "Use voucher", description = "Increment the used count of a voucher")
    public ResponseEntity<VoucherResponse> useVoucher(
            @Parameter(description = "Voucher ID", required = true)
            @PathVariable Long id) {
        
        log.info("Using voucher with id: {}", id);
        VoucherResponse response = voucherService.incrementUsedCount(id);
        return ResponseEntity.ok(response);
    }
}