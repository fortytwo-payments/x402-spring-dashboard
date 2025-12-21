package io.x402.dashboard.buyer.service;

import io.x402.dashboard.buyer.domain.SpendingStatus;
import io.x402.dashboard.buyer.domain.X402SpendingEvent;
import io.x402.dashboard.buyer.repository.X402SpendingEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing X402SpendingEvent entities.
 */
@Service
@Transactional(readOnly = true)
public class X402SpendingEventService {

    private final X402SpendingEventRepository repository;

    public X402SpendingEventService(X402SpendingEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Save a spending event.
     */
    @Transactional
    public X402SpendingEvent save(X402SpendingEvent event) {
        return repository.save(event);
    }

    /**
     * Find spending event by ID.
     */
    public Optional<X402SpendingEvent> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Find all spending events.
     */
    public List<X402SpendingEvent> findAll() {
        return repository.findAll();
    }

    /**
     * Find spending events by buyer ID within time range.
     */
    public List<X402SpendingEvent> findByBuyerAndTimeRange(
            String buyerId,
            OffsetDateTime from,
            OffsetDateTime to) {
        return repository.findByBuyerIdAndCreatedAtBetween(buyerId, from, to);
    }

    /**
     * Find spending events with filters (paginated).
     */
    public Page<X402SpendingEvent> findWithFilters(
            String buyerId,
            String serviceId,
            SpendingStatus status,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable) {
        return repository.findWithFilters(buyerId, serviceId, status, null, from, to, pageable);
    }

    /**
     * Find recent spending events (latest 10).
     */
    public List<X402SpendingEvent> findRecent(String buyerId) {
        return repository.findTop10ByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    /**
     * Delete all spending events.
     */
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    /**
     * Count total spending events by buyer.
     */
    public Long countByBuyer(String buyerId, OffsetDateTime from, OffsetDateTime to) {
        return repository.countByBuyerIdAndCreatedAtBetween(buyerId, from, to);
    }
}
