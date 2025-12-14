package io.x402.dashboard.repository;

import io.x402.dashboard.domain.X402UsageEvent;
import io.x402.dashboard.domain.X402UsageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for X402UsageEvent entity.
 */
@Repository
public interface X402UsageEventRepository extends JpaRepository<X402UsageEvent, Long> {

    /**
     * Find recent events ordered by creation time descending.
     */
    List<X402UsageEvent> findTop100ByOrderByCreatedAtDesc();

    /**
     * Find events by tenant and time range.
     */
    @Query("""
        SELECT e FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.createdAt BETWEEN :from AND :to
        ORDER BY e.createdAt DESC
        """)
    List<X402UsageEvent> findByTenantAndTimeRange(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    /**
     * Find events with pagination.
     */
    @Query("""
        SELECT e FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND (:status IS NULL OR e.status = :status)
          AND e.createdAt BETWEEN :from AND :to
        ORDER BY e.createdAt DESC
        """)
    Page<X402UsageEvent> findEvents(
            @Param("tenantId") String tenantId,
            @Param("status") X402UsageStatus status,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable
    );

    /**
     * Aggregate by agent.
     * Returns: [agentId, count, sumAmount]
     */
    @Query("""
        SELECT e.agentId, COUNT(e), COALESCE(SUM(e.amountAtomic), 0)
        FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.createdAt >= :from AND e.createdAt <= :to
          AND (:status IS NULL OR e.status = :status)
        GROUP BY e.agentId
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> aggregateByAgent(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") X402UsageStatus status
    );

    /**
     * Aggregate by endpoint.
     * Returns: [endpoint, count, sumAmount]
     */
    @Query("""
        SELECT e.endpoint, COUNT(e), COALESCE(SUM(e.amountAtomic), 0)
        FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.createdAt >= :from AND e.createdAt <= :to
          AND (:status IS NULL OR e.status = :status)
        GROUP BY e.endpoint
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> aggregateByEndpoint(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") X402UsageStatus status
    );

    /**
     * Aggregate by status.
     * Returns: [status, count, sumAmount]
     */
    @Query("""
        SELECT e.status, COUNT(e), COALESCE(SUM(e.amountAtomic), 0)
        FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.createdAt >= :from AND e.createdAt <= :to
        GROUP BY e.status
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> aggregateByStatus(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    /**
     * Aggregate by date (daily).
     * Returns: [date, count, sumAmount]
     */
    @Query(value = """
        SELECT CAST(e.created_at AS DATE) as event_date, COUNT(*), COALESCE(SUM(e.amount_atomic), 0)
        FROM x402_usage_event e
        WHERE (:tenantId IS NULL OR e.tenant_id = :tenantId)
          AND e.created_at BETWEEN :from AND :to
          AND (:status IS NULL OR e.status = :status)
        GROUP BY CAST(e.created_at AS DATE)
        ORDER BY CAST(e.created_at AS DATE)
        """, nativeQuery = true)
    List<Object[]> aggregateByDate(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status
    );

    /**
     * Get total counts and amounts.
     * Returns: [totalCount, totalAmount, successCount, successAmount]
     */
    @Query("""
        SELECT
            COUNT(e),
            COALESCE(SUM(e.amountAtomic), 0),
            SUM(CASE WHEN e.status = io.x402.dashboard.domain.X402UsageStatus.SUCCESS THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.status = io.x402.dashboard.domain.X402UsageStatus.SUCCESS THEN COALESCE(e.amountAtomic, 0) ELSE 0 END)
        FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.createdAt BETWEEN :from AND :to
        """)
    Object[] getTotals(
            @Param("tenantId") String tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    /**
     * Count events by status in time range.
     */
    @Query("""
        SELECT COUNT(e) FROM X402UsageEvent e
        WHERE (:tenantId IS NULL OR e.tenantId = :tenantId)
          AND e.status = :status
          AND e.createdAt >= :from AND e.createdAt <= :to
        """)
    Long countByStatusAndTimeRange(
            @Param("tenantId") String tenantId,
            @Param("status") X402UsageStatus status,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
