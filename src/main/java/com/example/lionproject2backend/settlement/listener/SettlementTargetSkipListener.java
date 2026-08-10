package com.example.lionproject2backend.settlement.listener;

import com.example.lionproject2backend.settlement.domain.Settlement;
import com.example.lionproject2backend.settlement.domain.SettlementTarget;
import com.example.lionproject2backend.settlement.exception.SettlementTargetSkippableException;
import com.example.lionproject2backend.settlement.repository.SettlementTargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementTargetSkipListener implements SkipListener<SettlementTarget, Settlement> {

    private final SettlementTargetRepository settlementTargetRepository;

    @Override
    public void onSkipInProcess(SettlementTarget item, Throwable throwable) {
        SettlementTarget target = resolveTarget(item, throwable);
        target.markAsFailed();
        settlementTargetRepository.save(target);
        log.warn("정산 target 처리 실패로 skip - targetId={}, mentorId={}, reason={}",
                target.getId(),
                target.getMentorId(),
                throwable.getMessage()
        );
    }

    private SettlementTarget resolveTarget(SettlementTarget item, Throwable throwable) {
        if (throwable instanceof SettlementTargetSkippableException exception) {
            return exception.getTarget();
        }

        return item;
    }
}
