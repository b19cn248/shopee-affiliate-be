package com.ptit.shopeeaffiliatebe.repository;

import com.ptit.shopeeaffiliatebe.domain.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>, JpaSpecificationExecutor<Voucher> {
    
    boolean existsByCodeAndDeletedAtIsNull(String code);
    
    Optional<Voucher> findByIdAndDeletedAtIsNull(Long id);
    
    Optional<Voucher> findByCodeAndDeletedAtIsNull(String code);
    
    @Query("SELECT v FROM Voucher v WHERE v.id = :id")
    Optional<Voucher> findByIdIncludeDeleted(@Param("id") Long id);
    
    @Query("UPDATE Voucher v SET v.status = 'EXPIRED' WHERE v.endAt < :now AND v.status != 'EXPIRED' AND v.deletedAt IS NULL")
    void updateExpiredVouchers(@Param("now") LocalDateTime now);
}