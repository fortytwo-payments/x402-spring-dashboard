package io.x402.dashboard.buyer.repository;

import io.x402.dashboard.buyer.domain.ServiceCategory;
import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for X402SpendingEvent entity.
 * Provides queries for spending analytics from buyer's perspective.
 */
@Repository
public interface X402SpendingEventRepository extends JpaRepository<X402SpendingEvent, Long> {

    /**
     * Find all spending events by buyer ID within a time range.
     */
    List<X402SpendingEvent> findByBuyerIdAndCreatedAtBetween(
        String buyerId,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Find all spending events by buyer ID, status, and time range.
     */
    List<X402SpendingEvent> findByBuyerIdAndStatusAndCreatedAtBetween(
        String buyerId,
        SpendingStatus status,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Find all spending events by service ID within a time range.
     */
    List<X402SpendingEvent> findByServiceIdAndCreatedAtBetween(
        String serviceId,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Find all spending events by category within a time range.
     */
    List<X402SpendingEvent> findByCategoryAndCreatedAtBetween(
        ServiceCategory category,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Find paginated spending events with optional filters.
     */
    @Query("SELECT e FROM X402SpendingEvent e WHERE " +
           "(:buyerId IS NULL OR e.buyerId = :buyerId) AND " +
           "(:serviceId IS NULL OR e.serviceId = :serviceId) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "e.createdAt BETWEEN :from AND :to " +
           "ORDER BY e.createdAt DESC")
    Page<X402SpendingEvent> findWithFilters(
        @Param("buyerId") String buyerId,
        @Param("serviceId") String serviceId,
        @Param("status") SpendingStatus status,
        @Param("category") ServiceCategory category,
        @Param("from") OffsetDateTime from,
        @Param("to") OffsetDateTime to,
        Pageable pageable
    );

    /**
     * Calculate total spending by buyer within a time range.
     */
    @Query("SELECT COALESCE(SUM(e.amountAtomic), 0) FROM X402SpendingEvent e WHERE " +
           "e.buyerId = :buyerId AND " +
           "e.status = 'SUCCESS' AND " +
           "e.createdAt BETWEEN :from AND :to")
    Long sumAmountByBuyerAndTimeRange(
        @Param("buyerId") String buyerId,
        @Param("from") OffsetDateTime from,
        @Param("to") OffsetDateTime to
    );

    /**
     * Count total spending events by buyer within a time range.
     */
    Long countByBuyerIdAndCreatedAtBetween(
        String buyerId,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Count successful spending events by buyer within a time range.
     */
    Long countByBuyerIdAndStatusAndCreatedAtBetween(
        String buyerId,
        SpendingStatus status,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Find recent spending events (latest N events).
     */
    List<X402SpendingEvent> findTop10ByBuyerIdOrderByCreatedAtDesc(String buyerId);

    /**
     * Find spending events by budget ID.
     */
    List<X402SpendingEvent> findByBudgetIdAndCreatedAtBetween(
        String budgetId,
        OffsetDateTime from,
        OffsetDateTime to
    );

    /**
     * Calculate total spending by budget ID.
     */
    @Query("SELECT COALESCE(SUM(e.amountAtomic), 0) FROM X402SpendingEvent e WHERE " +
           "e.budgetId = :budgetId AND " +
           "e.status = 'SUCCESS' AND " +
           "e.createdAt BETWEEN :from AND :to")
    Long sumAmountByBudgetAndTimeRange(
        @Param("budgetId") String budgetId,
        @Param("from") OffsetDateTime from,
        @Param("to") OffsetDateTime to
    );
}
