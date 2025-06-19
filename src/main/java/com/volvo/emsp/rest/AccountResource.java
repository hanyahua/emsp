package com.volvo.emsp.rest;

import com.volvo.emsp.application.command.ChangeAccountStatusCommand;
import com.volvo.emsp.application.command.CreateAccountCommand;
import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.application.service.AccountApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class AccountResource {

    private final AccountApplicationService accountApplicationService;

    public AccountResource(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping("/api/accounts")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody CreateAccountCommand command) {
        AccountDTO accountDTO = accountApplicationService.createAccount(command.getEmail());
        URI location = URI.create("/api/accounts/" + accountDTO.getAccountId());
        return ResponseEntity.created(location).body(accountDTO);
    }

    @GetMapping ("/api/accounts")
    public ResponseEntity<Page<AccountDTO>> findAccounts(
            @RequestParam(value = "lastUpdatedFrom", required = false) LocalDateTime from,
            @RequestParam(value = "lastUpdatedTo", required = false) LocalDateTime to,
            @RequestParam(name = "pageNumber", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdated").descending());
        Page<AccountDTO> accountDTOS = accountApplicationService.findAccounts(from, to, pageable);
        return ResponseEntity.ok(accountDTOS);
    }

    @GetMapping ("/api/accounts/{id}")
    public ResponseEntity<AccountDTO> findAccount(@PathVariable Long id) {
        Optional<AccountDTO> optionalAccountDTO = accountApplicationService.findAccountById(id);
        return optionalAccountDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping ("/api/accounts/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeAccountStatusCommand request
    ) {
        accountApplicationService.changeAccountStatus(id, request.getTargetStatus());
        return ResponseEntity.noContent().build();
    }
}
