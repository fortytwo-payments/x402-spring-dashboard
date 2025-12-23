package io.x402.dashboard.seller.service;

import io.x402.dashboard.seller.domain.X402UsageEvent;
import io.x402.dashboard.seller.domain.X402UsageStatus;
import io.x402.dashboard.seller.repository.X402UsageEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing X402UsageEvent entities.
 */
@Service
public class X402UsageEventService {

    private final X402UsageEventRepository repository;

    public X402UsageEventService(X402UsageEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Save an event.
     */
    @Transactional
    public X402UsageEvent save(X402UsageEvent event) {
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(OffsetDateTime.now());
        }
        return repository.save(event);
    }

    /**
     * Find event by ID.
     */
    public Optional<X402UsageEvent> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Get recent events (up to limit).
     */
    public List<X402UsageEvent> getRecent(int limit) {
        List<X402UsageEvent> list = repository.findTop100ByOrderByCreatedAtDesc();
        return list.size() > limit ? list.subList(0, limit) : list;
    }

    /**
     * Get events by tenant and time range.
     */
    public List<X402UsageEvent> findByTenantAndTimeRange(
            String tenantId,
            OffsetDateTime from,
            OffsetDateTime to) {
        return repository.findByTenantAndTimeRange(tenantId, from, to);
    }

    /**
     * Get events with pagination.
     */
    public Page<X402UsageEvent> findEvents(
            String tenantId,
            X402UsageStatus status,
            OffsetDateTime from,
            OffsetDateTime to,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findEvents(tenantId, status, from, to, pageable);
    }

    /**
     * Count events by status in time range.
     */
    public Long countByStatusAndTimeRange(
            String tenantId,
            X402UsageStatus status,
            OffsetDateTime from,
            OffsetDateTime to) {
        return repository.countByStatusAndTimeRange(tenantId, status, from, to);
    }

    /**
     * Delete event by ID.
     */
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    /**
     * Delete all events.
     */
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
