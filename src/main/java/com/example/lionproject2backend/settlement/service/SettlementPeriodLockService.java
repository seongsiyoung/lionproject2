package com.example.lionproject2backend.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementPeriodLockService {

    private static final String LOCK_KEY_PREFIX = "settlement:create:";

    private final RedissonClient redissonClient;
    private final Map<Long, RLock> acquiredLocks = new ConcurrentHashMap<>();

    public void acquire(String settlementPeriod, Long jobExecutionId) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + settlementPeriod);

        try {
            boolean acquired = lock.tryLock(0, -1, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new IllegalStateException("이미 실행 중인 정산 배치가 있습니다. settlementPeriod=" + settlementPeriod);
            }
            acquiredLocks.put(jobExecutionId, lock);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("정산 기간 락 획득 중 인터럽트가 발생했습니다. settlementPeriod=" + settlementPeriod, e);
        }
    }

    public void release(String settlementPeriod, Long jobExecutionId) {
        RLock lock = acquiredLocks.remove(jobExecutionId);
        if (lock == null) {
            log.debug("해제할 정산 기간 락이 없습니다. settlementPeriod={}, jobExecutionId={}", settlementPeriod, jobExecutionId);
            return;
        }

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            return;
        }

        log.warn("현재 스레드가 소유하지 않은 정산 기간 락은 해제하지 않습니다. settlementPeriod={}, jobExecutionId={}", settlementPeriod, jobExecutionId);
    }
}
