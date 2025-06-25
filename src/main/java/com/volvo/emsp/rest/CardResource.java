package com.volvo.emsp.rest;

import com.volvo.emsp.application.command.ChangeCardStatusCommand;
import com.volvo.emsp.application.command.CreateCardCommand;
import com.volvo.emsp.application.dto.CardDTO;
import com.volvo.emsp.application.service.CardApplicationService;
import com.volvo.emsp.domain.model.enums.CardStatus;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Tag(name = "Card Management", description = "APIs for managing card operations")
public class CardResource {

    private static final Logger log = LoggerFactory.getLogger(CardResource.class);
    private final CardApplicationService cardApplicationService;

    public CardResource(CardApplicationService cardApplicationService) {
        this.cardApplicationService = cardApplicationService;
    }

    @Operation(summary = "Create card",
            description = "Create a new card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "title": "Validation Failed",
                                      "details": [
                                        "Invalid card data"
                                      ],
                                      "path": "/api/cards",
                                      "timestamp": "2025-06-24T15:16:20.379Z"
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/api/cards")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateCardCommand createCardCommand) {
        CardDTO cardDTO = cardApplicationService.createCard(createCardCommand);
        URI uri = URI.create("/api/cards/" + cardDTO.getCardId());
        return ResponseEntity.created(uri).body(cardDTO);
    }

    @Operation(summary = "Update card status",
            description = """
                   Change card status (activate, deactivate, or assign to account).</br>
                   The targetStatus to change the card to, Valid values are ASSIGNED, ACTIVATED, DEACTIVATED.</br>
                   The assignToAccount is required when targetStatus is ASSIGNED
                   """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "title": "Validation Failed",
                                      "details": [
                                        "Invalid card status: ASSIG88NED"
                                      ],
                                      "path": "/api/cards/1/status",
                                      "timestamp": "2025-06-24T16:49:37.635Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Card or Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "title": "Resource not found",
                                      "details": [
                                        "Card not found: 2323"
                                      ],
                                      "path": "/api/cards/2323/status",
                                      "timestamp": "2025-06-24T16:29:38.244Z"
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
                                        "Only cards in CREATED state can be assigned."
                                      ],
                                      "path": "/api/cards/1/status",
                                      "timestamp": "2025-06-24T16:46:16.255Z"
                                    }
                                    """
                            )
                    )
            )
    })
    @PatchMapping("/api/cards/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeCardStatusCommand command
    ) {
        CardStatus targetStatus;
        if (command == null) {
            throw new IllegalArgumentException("Invalid command");
        }
        try {
            targetStatus = CardStatus.valueOf(command.getTargetStatus().toUpperCase());
        } catch (Exception e) {
            log.error("Invalid card status: {}", command.getTargetStatus(), e);
            throw new IllegalArgumentException("Invalid card status: " + command.getTargetStatus());
        }
        switch (targetStatus) {
            case ACTIVATED -> cardApplicationService.activeCard(id);
            case DEACTIVATED -> cardApplicationService.deactivateCard(id);
            case ASSIGNED -> cardApplicationService.assignCardToAccount(id, command.getAssignToAccount());
            default -> throw new InvalidBusinessOperationException("Can not change card status to: " + command.getTargetStatus());
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get cards",
            description = "Retrieve a paginated list of cards with optional time range filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the card list",
                    content = @Content(schema = @Schema(implementation = CardPagedSchema.class))),
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
                                      "path": "/api/cards",
                                      "timestamp": "2025-06-24T15:23:45.740Z"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/api/cards")
    public ResponseEntity<PagedModel<CardDTO>> findCards(
            @Parameter(description = "Start date-time for last updated filter (Format: yyyy-MM-dd'T'HH:mm:ssZ)",
                    example = "2025-05-01T10:15:30Z")
            @RequestParam(value = "lastUpdatedFrom", required = false) OffsetDateTime from,
            @Parameter(description = "End date-time for last updated filter (Format: yyyy-MM-dd'T'HH:mm:ssZ)",
                    example = "2025-12-31T10:15:30Z")
            @RequestParam(value = "lastUpdatedTo", required = false) OffsetDateTime to,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "pageNumber", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(name = "pageSize", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdated").descending());
        Page<CardDTO> cardDTOS = cardApplicationService.findCards(from, to, pageable);
        return ResponseEntity.ok(new PagedModel<>(cardDTOS));
    }

    @Operation(summary = "Get card by ID",
            description = "Retrieve a card by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the card"),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "title": "Resource not found",
                                      "details": [
                                        "Card not found: 999"
                                      ],
                                      "path": "/api/cards/999",
                                      "timestamp": "2025-06-24T15:25:16.140Z"
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/api/cards/{id}")
    public ResponseEntity<CardDTO> findCard(@PathVariable Long id) {
        Optional<CardDTO> optionalCard = cardApplicationService.findCardById(id);
        return optionalCard.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));
    }

    // for doc schema
    private static class CardPagedSchema extends PagedModel<CardDTO> {
        public CardPagedSchema(Page<CardDTO> page) {
            super(page);
        }
    }
}