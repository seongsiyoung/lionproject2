package com.example.lionproject2backend.settlement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementPeriodLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @InjectMocks
    private SettlementPeriodLockService lockService;

    @Test
    void redisson_tryLock이_true면_정산월_락을_획득한다() throws Exception {
        when(redissonClient.getLock("settlement:create:2026-07")).thenReturn(lock);
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenReturn(true);

        lockService.acquire("2026-07", 1L);

        verify(lock).tryLock(0, -1, TimeUnit.MILLISECONDS);
    }

    @Test
    void redisson_tryLock이_false면_같은_정산월_배치가_실행중인_것으로_보고_실패한다() throws Exception {
        when(redissonClient.getLock("settlement:create:2026-07")).thenReturn(lock);
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenReturn(false);

        assertThatThrownBy(() -> lockService.acquire("2026-07", 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 실행 중인 정산 배치");
    }

    @Test
    void redisson_tryLock이_interrupt되면_interrupt_상태를_복구하고_실패한다() throws Exception {
        when(redissonClient.getLock("settlement:create:2026-07")).thenReturn(lock);
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException("interrupted"));

        assertThatThrownBy(() -> lockService.acquire("2026-07", 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("정산 기간 락 획득 중 인터럽트");

        assertThat(Thread.currentThread().isInterrupted()).isTrue();
        Thread.interrupted();
    }

    @Test
    void release는_현재_스레드가_보유한_redisson_lock만_unlock한다() throws Exception {
        when(redissonClient.getLock("settlement:create:2026-07")).thenReturn(lock);
        when(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        lockService.acquire("2026-07", 1L);
        lockService.release("2026-07", 1L);

        verify(lock).unlock();
    }

    @Test
    void release할_락이_없으면_unlock하지_않는다() {
        lockService.release("2026-07", 1L);

        verify(lock, never()).unlock();
    }
}
