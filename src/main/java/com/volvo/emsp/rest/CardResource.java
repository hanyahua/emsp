package com.volvo.emsp.rest;

import com.volvo.emsp.application.command.ChangeCardStatusCommand;
import com.volvo.emsp.application.command.CreateCardCommand;
import com.volvo.emsp.application.dto.CardDTO;
import com.volvo.emsp.application.service.CardApplicationService;
import com.volvo.emsp.domain.model.enums.CardStatus;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CardResource {

    private static final Logger log = LoggerFactory.getLogger(CardResource.class);
    private final CardApplicationService cardApplicationService;

    @SuppressWarnings("unused")
    public CardResource(CardApplicationService cardApplicationService) {
        this.cardApplicationService = cardApplicationService;
    }

    @PostMapping("/api/cards")
    private ResponseEntity<CardDTO> createCard(@Valid @RequestBody CreateCardCommand createCardCommand) {
        CardDTO cardDTO = cardApplicationService.createCard(createCardCommand);
        URI uri = URI.create("/api/cards/" + cardDTO.getCardId());
        return ResponseEntity.created(uri).body(cardDTO);
    }

    @PatchMapping ("/api/cards/{id}/status")
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

    @GetMapping("/api/cards")
    public ResponseEntity<Page<CardDTO>> findCards(
            @RequestParam(value = "lastUpdatedFrom", required = false) LocalDateTime from,
            @RequestParam(value = "lastUpdatedTo", required = false) LocalDateTime to,
            @RequestParam(name = "pageNumber", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdated").descending());
        Page<CardDTO> cardDTOS = cardApplicationService.findCards(from, to, pageable);
        return ResponseEntity.ok(cardDTOS);
    }

    @GetMapping("/api/cards/{id}")
    public ResponseEntity<CardDTO> findCard(@PathVariable Long id) {
        Optional<CardDTO> optionalCard = cardApplicationService.findCardById(id);
        return optionalCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
