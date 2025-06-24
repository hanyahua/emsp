package com.volvo.emsp.rest.integration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.emsp.application.command.ChangeAccountStatusCommand;
import com.volvo.emsp.application.command.CreateAccountCommand;
import com.volvo.emsp.application.command.CreateCardCommand;
import com.volvo.emsp.application.command.ChangeCardStatusCommand;
import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.application.dto.CardDTO;
import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.domain.model.enums.CardStatus;
import com.volvo.emsp.testmodel.Emails;
import com.volvo.emsp.testmodel.PageTestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@ActiveProfiles("integration-test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CardResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String RFID_UID = "rfidUid0001";
    private static final String VISIBLE_NUMBER = "0001";

    @BeforeEach
    void setUp() throws Exception {
        // first: create a card
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid(RFID_UID);
        command.setVisibleNumber(VISIBLE_NUMBER);
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }

    @Test
    void createCardOk() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid("rfidUid0002");
        command.setVisibleNumber("0002");
        MvcResult result = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        assertThat(location).matches("/api/cards/\\d+");
    }

    @Test
    void createCardWithoutRfidUid() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        //command.setRfidUid("rfidUid0002");
        command.setVisibleNumber("0002");
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCardWithoutVisibleNumber() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid("rfidUid0002");
        //command.setVisibleNumber("0002");
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCardTooLongVisibleNumber() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid("rfidUid0002");
        command.setVisibleNumber("0000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002");
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCardTooLongRfidUid() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid("rfidUid00000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000000000000002");
        command.setVisibleNumber("0002");
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCardWithExistsRfidUid() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid(RFID_UID);
        command.setVisibleNumber("0002");
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict());
    }

    @Test
    void createCardWithExistsVisibleNumber() throws Exception {
        CreateCardCommand command = new CreateCardCommand();
        command.setRfidUid("rfidUid0002");
        command.setVisibleNumber(VISIBLE_NUMBER);
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict());
    }

    @Test
    void findCardsWithoutLastUpdated() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cards")
                        .param("pageNumber", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PageTestModel<?> page = objectMapper.readValue(content, PageTestModel.class);
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getPage().getTotalElements()).isGreaterThan(0);
    }

    @Test
    void findCardsWithDateRangeOk() throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        MvcResult result = mockMvc.perform(get("/api/cards")
                        .param("lastUpdatedFrom", from.toString())
                        .param("lastUpdatedTo", to.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PageTestModel<?> page = objectMapper.readValue(content, PageTestModel.class);
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void findCardsWithDateRangeShouldNotFilterResults() throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().minusDays(1);
        MvcResult result = mockMvc.perform(get("/api/cards")
                        .param("lastUpdatedFrom", from.toString())
                        .param("lastUpdatedTo", to.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        PageTestModel<?> page = objectMapper.readValue(content, PageTestModel.class);
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    void assignCardOK() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // create an account
        Long accountId = createAnNewAccountAndGetId();
        // active an account
        activateAccount(accountId);
        // assign card to account
        assignCardToAccount(cardId, accountId);
    }

    @Test
    void activateCardOk() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // create and activate an account first
        Long accountId = createAnNewAccountAndGetId();
        // activate the account
        activateAccount(accountId);
        // assign card to account
        assignCardToAccount(cardId, accountId);
        // change card status to ACTIVATED
        activateCard(cardId);
        // verify the status change
        mockMvc.perform(get("/api/cards/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CardStatus.ACTIVATED.name()));
    }

    @Test
    void deactivateCardOk() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // create and activate an account first
        Long accountId = createAnNewAccountAndGetId();
        // activate the account
        activateAccount(accountId);
        // assign card to account
        assignCardToAccount(cardId, accountId);
        // activate card
        activateCard(cardId);
        // change card status to DEACTIVATED
        deactivateCard(cardId);
    }

    @Test
    void assignCardWithoutAccountId() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // assign card to account
        ChangeCardStatusCommand assignCommand = new ChangeCardStatusCommand();
        assignCommand.setTargetStatus(CardStatus.ASSIGNED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignCardToUnactivatedAccount() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // assign card to account
        Long accountId = createAnNewAccountAndGetId();
        // attempt to assign card to the unactivated account
        ChangeCardStatusCommand assignCommand = new ChangeCardStatusCommand();
        assignCommand.setAssignToAccount(accountId);
        assignCommand.setTargetStatus(CardStatus.ASSIGNED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignCommand)))
                .andExpect(status().isConflict());
    }

    @Test
    void assignCardToDeactivatedAccount() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // assign card to account
        Long accountId = createAnNewAccountAndGetId();
        deactivateAccount(accountId);
        // attempt to assign card to the unactivated account
        ChangeCardStatusCommand assignCommand = new ChangeCardStatusCommand();
        assignCommand.setAssignToAccount(accountId);
        assignCommand.setTargetStatus(CardStatus.ASSIGNED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignCommand)))
                .andExpect(status().isConflict());
    }

    @Test
    void assignAssignedCardToAccount() throws Exception {
        // first get a card ID
        Long cardId = fetchACardId();
        // assign card to account
        Long accountId = createAnNewAccountAndGetId();
        activateAccount(accountId);
        assignCardToAccount(cardId, accountId);
        Long accountId2 = createAnNewAccountAndGetId(Emails.EMAIL2);
        // attempt to assign card to the unactivated account
        ChangeCardStatusCommand assignCommand = new ChangeCardStatusCommand();
        assignCommand.setAssignToAccount(accountId2);
        assignCommand.setTargetStatus(CardStatus.ASSIGNED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignCommand)))
                .andExpect(status().isConflict());
    }

    private Long createAnNewAccountAndGetId() throws Exception {
        return createAnNewAccountAndGetId(Emails.EMAIL1);
    }

    private Long createAnNewAccountAndGetId(String email) throws Exception {
        CreateAccountCommand createAccountCommand = new CreateAccountCommand();
        createAccountCommand.setEmail(email);
        MvcResult accountResult = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountCommand)))
                .andExpect(status().isCreated()).andReturn();
        AccountDTO accountDTO = objectMapper.readValue(
                accountResult.getResponse().getContentAsString(), AccountDTO.class);
        return accountDTO.getAccountId();
    }

    private Long fetchACardId() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/api/cards"))
                .andReturn();
        String content = listResult.getResponse().getContentAsString();
        PageTestModel<CardDTO> page = objectMapper.readValue(content, new TypeReference<>() {});
        return page.getContent().getFirst().getCardId();
    }

    private void activateAccount(Long accountId) throws Exception {
        ChangeAccountStatusCommand changeAccountStatusCommand = new ChangeAccountStatusCommand();
        changeAccountStatusCommand.setTargetStatus(AccountStatus.ACTIVATED.name());
        mockMvc.perform(patch("/api/accounts/" + accountId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeAccountStatusCommand)))
                .andExpect(status().isNoContent());
    }

    private void deactivateAccount(Long accountId) throws Exception {
        ChangeAccountStatusCommand changeAccountStatusCommand = new ChangeAccountStatusCommand();
        changeAccountStatusCommand.setTargetStatus(AccountStatus.DEACTIVATED.name());
        mockMvc.perform(patch("/api/accounts/" + accountId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeAccountStatusCommand)))
                .andExpect(status().isNoContent());
    }

    private void assignCardToAccount(Long cardId, Long accountId) throws Exception {
        ChangeCardStatusCommand assignCommand = new ChangeCardStatusCommand();
        assignCommand.setAssignToAccount(accountId);
        assignCommand.setTargetStatus(CardStatus.ASSIGNED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignCommand)))
                .andExpect(status().isNoContent());
    }

    private void activateCard(Long cardId) throws Exception {
        ChangeCardStatusCommand command = new ChangeCardStatusCommand();
        command.setTargetStatus(CardStatus.ACTIVATED.name());
        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
    }

    private void deactivateCard(Long cardId) throws Exception {
        ChangeCardStatusCommand changeCardStatusCommand = new ChangeCardStatusCommand();
        changeCardStatusCommand.setTargetStatus(CardStatus.DEACTIVATED.name());
        mockMvc.perform(patch("/api/cards/" + cardId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeCardStatusCommand)))
                .andExpect(status().isNoContent());
        // verify the status change
        mockMvc.perform(get("/api/cards/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CardStatus.DEACTIVATED.name()));
    }
}