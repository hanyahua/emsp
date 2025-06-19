package com.volvo.emsp.rest.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.emsp.application.command.ChangeAccountStatusCommand;
import com.volvo.emsp.application.command.CreateAccountCommand;
import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.domain.model.enums.AccountStatus;
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // rollback
public class AccountResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() throws Exception {
        // first: create an account
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail(testEmail);
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }

    @Test
    void createAccountOk() throws Exception {
        String newEmail = "new@example.com";
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail(newEmail);
        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).matches("/api/accounts/\\d+");
    }

    @Test
    void createAccountWithInvalidEmail() throws Exception {
        String newEmail = Emails.INVALID_EMAIL;
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail(newEmail);
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccountWithTooLongEmail() throws Exception {
        String newEmail = Emails.TOO_LONG_EMAIL;
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail(newEmail);
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccountWithExistsEmail() throws Exception {
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail(testEmail);
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict());
    }

    @Test
    void findAccountsWithoutDateRange() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/accounts")
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
    void findAccountsWithDateRangeShouldFilterResults() throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        MvcResult result = mockMvc.perform(get("/api/accounts")
                        .param("lastUpdatedTimeFrom", from.toString())
                        .param("lastUpdatedTimeTo", to.toString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageTestModel<?> page = objectMapper.readValue(content, PageTestModel.class);

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void findAccountsWithDateRangeShouldNotFilterResults() throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().minusDays(1);

        MvcResult result = mockMvc.perform(get("/api/accounts")
                        .param("lastUpdatedTimeFrom", from.toString())
                        .param("lastUpdatedTimeTo", to.toString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PageTestModel<?> page = objectMapper.readValue(content, PageTestModel.class);

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void activate() throws Exception {
        // first get an account ID
        final Long accountId = fetchAnAccountId();
        // change status
        activateAccount(accountId);
    }

    @Test
    void deactivate() throws Exception {
        // first get an account ID
        Long accountId = fetchAnAccountId();
        // change status
        deactivateAccount(accountId);
    }

    @Test
    void changeAccountStatusToCreated() throws Exception {
        // first get an account ID
        Long accountId = fetchAnAccountId();

        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setTargetStatus(AccountStatus.CREATED.name());

        mockMvc.perform(patch("/api/accounts/{id}/status", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict());
    }

    @Test
    void changeAccountStatusToErrorStatus() throws Exception {
        // first get an account ID
        Long accountId = fetchAnAccountId();

        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setTargetStatus("something");

        mockMvc.perform(patch("/api/accounts/{id}/status", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    private Long fetchAnAccountId() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/api/accounts"))
                .andReturn();
        String content = listResult.getResponse().getContentAsString();
        PageTestModel<AccountDTO> page = objectMapper.readValue(content, new TypeReference<>() {});
        return page.getContent().getFirst().getAccountId();
    }

    private void activateAccount(Long accountId) throws Exception {
        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setTargetStatus(AccountStatus.ACTIVATED.name());

        mockMvc.perform(patch("/api/accounts/{id}/status", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVATED.name()));
    }

    private void deactivateAccount(Long accountId) throws Exception {
        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setTargetStatus(AccountStatus.DEACTIVATED.name());

        mockMvc.perform(patch("/api/accounts/{id}/status", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccountStatus.DEACTIVATED.name()));
    }
}
