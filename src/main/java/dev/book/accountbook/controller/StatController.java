package dev.book.accountbook.controller;

import dev.book.accountbook.controller.swagger.StatApi;
import dev.book.accountbook.dto.response.AccountBookConsumeResponse;
import dev.book.accountbook.dto.response.AccountBookSpendResponse;
import dev.book.accountbook.dto.response.AccountBookStatResponse;
import dev.book.accountbook.service.StatService;
import dev.book.accountbook.type.Category;
import dev.book.accountbook.type.Frequency;
import dev.book.global.config.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController implements StatApi {
    private final StatService statService;

    @Override
    public ResponseEntity<List<AccountBookStatResponse>> statList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Frequency frequency) {
        Long userId = userDetails.user().getId();
        List<AccountBookStatResponse> list = statService.statList(userId, frequency);

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<AccountBookSpendResponse>> categoryList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Frequency frequency, @PathVariable Category category) {
        Long userId = userDetails.user().getId();
        List<AccountBookSpendResponse> list = statService.categoryList(userId, frequency, category);

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<AccountBookConsumeResponse> consume(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Frequency frequency, @PathVariable Category category) {
        Long userId = userDetails.user().getId();
        AccountBookConsumeResponse response = statService.consume(userId, frequency, category);

        return ResponseEntity.ok(response);
    }
}
