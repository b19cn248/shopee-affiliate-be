package com.ptit.shopeeaffiliatebe.domain.entity;

import com.ptit.shopeeaffiliatebe.domain.enums.DiscountType;
import com.ptit.shopeeaffiliatebe.domain.enums.Platform;
import com.ptit.shopeeaffiliatebe.domain.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 64)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "voucher_platform")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Platform platform;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, columnDefinition = "voucher_discount_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "min_order_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "voucher_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private VoucherStatus status;
    
    @Column(name = "tags", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] tags;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (usedCount == null) {
            usedCount = 0;
        }
        if (minOrderAmount == null) {
            minOrderAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = VoucherStatus.DRAFT;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == VoucherStatus.ACTIVE 
            && !isDeleted()
            && now.isAfter(startAt) 
            && now.isBefore(endAt)
            && (usageLimit == null || usedCount < usageLimit);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endAt);
    }
}