package dev.book.accountbook.service;

import dev.book.accountbook.dto.event.SpendCreatedEvent;
import dev.book.accountbook.dto.request.BudgetRequest;
import dev.book.accountbook.dto.response.BudgetResponse;
import dev.book.accountbook.entity.Budget;
import dev.book.accountbook.exception.accountbook.AccountBookErrorCode;
import dev.book.accountbook.exception.accountbook.AccountBookErrorException;
import dev.book.accountbook.repository.BudgetRepository;
import dev.book.global.config.Firebase.entity.FcmToken;
import dev.book.global.config.Firebase.exception.FcmTokenErrorCode;
import dev.book.global.config.Firebase.exception.FcmTokenErrorException;
import dev.book.global.config.Firebase.repository.FcmTokenRepository;
import dev.book.global.config.Firebase.service.FCMService;
import dev.book.user.entity.UserEntity;
import dev.book.user.exception.UserErrorCode;
import dev.book.user.exception.UserErrorException;
import dev.book.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final FcmTokenRepository fcmTokenRepository;

    private final FCMService fcmService;

    public BudgetResponse getBudget(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserErrorException(UserErrorCode.USER_NOT_FOUND));

        return budgetRepository.findBudgetWithTotal(user.getId());
    }

    public BudgetResponse createBudget(UserEntity user, BudgetRequest budgetRequest) {
        int date = LocalDate.now().getMonthValue();
        budgetRepository.save(new Budget(budgetRequest.budget(), null, date, user));

        return budgetRepository.findBudgetWithTotal(user.getId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendLimitWarning(SpendCreatedEvent event) {
        BudgetResponse response = budgetRepository.findBudgetWithTotal(event.userId());

        if (response.total() >= response.budget() * 0.5) {
            long usageRate = calcUsageRate(response);

            FcmToken token = fcmTokenRepository.findByUserId(event.userId())
                    .orElseThrow(() -> new FcmTokenErrorException(FcmTokenErrorCode.NOT_FOUND_FCM_TOKEN));

            fcmService.sendSpendNotification(token.getToken(), event.nickname(), response.budget(), response.total(), usageRate);
        }
    }

    @Transactional
    public BudgetResponse modify(Long userId, Long id, BudgetRequest budgetRequest) {
        Budget budget = findBudgetIdAndUserId(id, userId);
        budget.modifyBudget(budgetRequest.budget());
        budgetRepository.flush();

        return budgetRepository.findBudgetWithTotal(userId);
    }

    public void deleteBudget(Long userId, Long id) {
        Budget budget = findBudgetIdAndUserId(id, userId);
        budgetRepository.deleteById(budget.getId());
    }

    private long calcUsageRate(BudgetResponse response) {
        return  (response.total() / response.budget()) * 100;
    }

    private Budget findBudgetIdAndUserId(Long budgetId, Long userId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId).orElseThrow(() -> new AccountBookErrorException(AccountBookErrorCode.NOT_FOUND_BUDGET));
    }
}
