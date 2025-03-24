package com.app.MediQuirk.repository;

import com.app.MediQuirk.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByOrderId(long orderId);
    Optional<Orders> findByOrderNumber(String orderNumber);

    Page<Orders> findByUserUserId(Long userId, Pageable pageable);

    long countByUserUserId(Long userId);

    Optional<Orders> findTopByUserUserIdOrderByOrderDateDesc(Long userId);
}
