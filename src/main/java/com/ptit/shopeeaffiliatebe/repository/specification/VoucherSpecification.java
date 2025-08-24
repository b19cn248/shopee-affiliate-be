package com.ptit.shopeeaffiliatebe.repository.specification;

import com.ptit.shopeeaffiliatebe.domain.entity.Voucher;
import com.ptit.shopeeaffiliatebe.domain.enums.Platform;
import com.ptit.shopeeaffiliatebe.domain.enums.VoucherStatus;
import com.ptit.shopeeaffiliatebe.dto.request.VoucherQueryRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VoucherSpecification {
    
    public static Specification<Voucher> buildSpecification(VoucherQueryRequest query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Exclude deleted by default
            if (query.getIncludeDeleted() == null || !query.getIncludeDeleted()) {
                predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            }
            
            // Filter by status
            if (query.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), query.getStatus()));
            }
            
            // Filter by platform
            if (query.getPlatform() != null) {
                predicates.add(criteriaBuilder.equal(root.get("platform"), query.getPlatform()));
            }
            
            // Search by code or title
            if (query.getSearchQuery() != null && !query.getSearchQuery().trim().isEmpty()) {
                String searchPattern = "%" + query.getSearchQuery().toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("code")), searchPattern
                );
                Predicate titlePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")), searchPattern
                );
                predicates.add(criteriaBuilder.or(codePredicate, titlePredicate));
            }
            
            // Filter active vouchers
            if (query.getActiveNow() != null && query.getActiveNow()) {
                LocalDateTime now = LocalDateTime.now();
                predicates.add(criteriaBuilder.equal(root.get("status"), VoucherStatus.ACTIVE));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startAt"), now));
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endAt"), now));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<Voucher> hasStatus(VoucherStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    
    public static Specification<Voucher> hasPlatform(Platform platform) {
        return (root, query, cb) -> cb.equal(root.get("platform"), platform);
    }
    
    public static Specification<Voucher> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }
    
    public static Specification<Voucher> searchByCodeOrTitle(String searchQuery) {
        return (root, query, cb) -> {
            String pattern = "%" + searchQuery.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("code")), pattern),
                cb.like(cb.lower(root.get("title")), pattern)
            );
        };
    }
}