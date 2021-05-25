package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class ResetPasswordTest extends AbstractRapidAuthIntegrationTest {

    // todo maybe add mod token tests like in changeEmailTests
//    @Autowired
//    private JweTokenService jweTokenService;

    private ResetPasswordDto resetPasswordDto(String newPassword) throws JsonProcessingException {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setNewPassword(newPassword);
        return dto;
    }

    @Test
    public void canResetPasswordWithCorrectCode() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        testTemplate.resetPasswordWithLink(resetPasswordDto(NEW_PASSWORD), mailData.getLink())
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(jsonPath("$.id").value(getUser().getId()));

        // New password should work
        login2xx(USER_EMAIL, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithSameCodeTwice() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        testTemplate.resetPasswordWithLink(resetPasswordDto(NEW_PASSWORD),mailData.getLink())
                .andExpect(status().is2xxSuccessful());

        // New password should work
        login2xx(USER_EMAIL, NEW_PASSWORD);

        // Repeating shouldn't work
        testTemplate.resetPasswordWithLink(resetPasswordDto(USER_PASSWORD), mailData.getLink())
                .andExpect(status().isForbidden());

        login2xx(USER_EMAIL, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithInvalidCode() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        String invalidCode = code +"invalid";
        testTemplate.resetPassword(resetPasswordDto(NEW_PASSWORD),invalidCode)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void cantResetPasswordWithInvalidPassword() throws Exception {
        // Blank password
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        testTemplate.resetPasswordWithLink(resetPasswordDto(""),mailData.getLink())
                .andExpect(status().isBadRequest());


        // Invalid password
        mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        testTemplate.resetPassword(resetPasswordDto(INVALID_PASSWORD),mailData.getLink())
                .andExpect(status().isBadRequest());
    }




}
