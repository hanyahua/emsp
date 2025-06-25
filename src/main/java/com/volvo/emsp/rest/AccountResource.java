package com.volvo.emsp.rest;

import com.volvo.emsp.application.command.ChangeAccountStatusCommand;
import com.volvo.emsp.application.command.CreateAccountCommand;
import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.application.service.AccountApplicationService;
import com.volvo.emsp.execption.ResourceNotFoundException;
import com.volvo.emsp.rest.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

@RestController
@Tag(name = "Account Management", description = "APIs for managing account operations")
public class AccountResource {

    private final AccountApplicationService accountApplicationService;

    public AccountResource(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Operation(summary = "Create account",
            description = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "title": "Validation Failed",
                                      "details": [
                                        "Invalid email format: user@example"
                                      ],
                                      "path": "/api/accounts",
                                      "timestamp": "2025-06-24T15:16:20.379+00:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Account with this email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "title": "Resource already exists",
                                      "details": [
                                        "Email already exists: user@example.com"
                                      ],
                                      "path": "/api/accounts",
                                      "timestamp": "2025-06-24T15:14:57.191+00:00"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/api/accounts")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody CreateAccountCommand command) {
        AccountDTO accountDTO = accountApplicationService.createAccount(command.getEmail());
        URI location = URI.create("/api/accounts/" + accountDTO.getAccountId());
        return ResponseEntity.created(location).body(accountDTO);
    }

    @Operation(summary = "Get accounts",
            description = "Retrieve a paginated list of accounts with optional time range filtering")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the account list",
                    content = @Content( schema = @Schema(implementation = AccountPagedSchema.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "title": "Validation Failed",
                                      "details": [
                                        "Page index must not be less than zero"
                                      ],
                                      "path": "/api/accounts",
                                      "timestamp": "2025-06-24T15:23:45.740+00:00"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping ("/api/accounts")
    public PagedModel<AccountDTO> findAccounts(
            @Parameter(description = "Start date-time for last updated filter (Format: yyyy-MM-dd'T'HH:mm:ssZ)",
                    example = "2025-05-01T10:15:30Z")
            @RequestParam(value = "lastUpdatedFrom", required = false) OffsetDateTime from,
            @Parameter(description = "End date-time for last updated filter (Format: yyyy-MM-dd'T'HH:mm:ssZ)",
                    example = "2025-12-31T10:15:30Z")
            @RequestParam(value = "lastUpdatedTo", required = false) OffsetDateTime to,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "pageNumber", defaultValue = "0") int page,
            @Parameter(description = "Size of each page", example = "10")
            @RequestParam(name = "pageSize", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdated").descending());
        Page<AccountDTO> accountDTOS = accountApplicationService.findAccounts(from, to, pageable);
        return new PagedModel<>(accountDTOS);
    }

    @Operation(summary = "Get account by ID",
            description = "Retrieve a specific account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "title": "Resource not found",
                                      "details": [
                                        "Account not found: 333"
                                      ],
                                      "path": "/api/accounts/333",
                                      "timestamp": "2025-06-24T16:02:47.747+00:00"
                                    }
                                    """
                            )
                    )

            )
    })
    @GetMapping ("/api/accounts/{id}")
    public ResponseEntity<AccountDTO> findAccount(@PathVariable Long id) {
        Optional<AccountDTO> optionalAccountDTO = accountApplicationService.findAccountById(id);
        return optionalAccountDTO.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    @Operation(summary = "Update account status",
            description = """
                    Activate or deactivate an account.</br>
                   The targetStatus to change the account to, Valid values are ACTIVATED, DEACTIVATED.
                   """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "title": "Resource not found",
                                      "details": [
                                        "Account not found: 2222"
                                      ],
                                      "path": "/api/accounts/2222/status",
                                      "timestamp": "2025-06-24T16:05:31.141+00:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid status transition",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "title": "Validation Failed",
                                      "details": [
                                        "Invalid account status: ACTIVATEDDDD"
                                      ],
                                      "path": "/api/accounts/1/status",
                                      "timestamp": "2025-06-24T16:07:12.726+00:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Invalid status transition",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "title": "Unsupported Operation",
                                      "details": [
                                        "Can not change account status to : CREATED"
                                      ],
                                      "path": "/api/accounts/1/status",
                                      "timestamp": "2025-06-25T01:12:26.708+00:00"
                                    }
                                    """
                            )
                    )
            )
    })
    @PatchMapping ("/api/accounts/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeAccountStatusCommand request
    ) {
        accountApplicationService.changeAccountStatus(id, request.getTargetStatus());
        return ResponseEntity.noContent().build();
    }

    // for doc schema
    public static class AccountPagedSchema extends PagedModel<AccountDTO> {

        public AccountPagedSchema(Page<AccountDTO> page) {
            super(page);
        }
    }
}
