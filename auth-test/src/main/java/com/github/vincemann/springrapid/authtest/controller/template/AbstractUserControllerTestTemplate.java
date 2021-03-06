package com.github.vincemann.springrapid.authtest.controller.template;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordView;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;

import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Activate spring Security, so login endpoint and auth web config is enabled, when using this template.
 *
 * @param <C>
 * @Override protected DefaultMockMvcBuilder createMvcBuilder() {
 * DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
 * mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
 * return mvcBuilder;
 * }
 */
public abstract class AbstractUserControllerTestTemplate<C extends AbstractUserController>
        extends AbstractCrudControllerTestTemplate<C> {


    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;


    private MailSender<MailData> mailSenderMock;

    @Autowired
    public void setMailSenderMock(MailSender<MailData> mailSenderMock) {
        this.mailSenderMock = mailSenderMock;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        super.setMvc(mvc);
    }

    public RequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

    public MailData signup2xx(Object dto) throws Exception {
        mvc.perform(signup(dto))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder resendVerificationMail(Serializable id, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getResendVerificationEmailUrl())
                .param("id",id.toString())
                .header(HttpHeaders.AUTHORIZATION, token);
    }

//    public MockHttpServletRequestBuilder login(String email, String password) {
//        return login(new LoginDto(email,password));
//    }

    public RequestBuilder login(String email, String password) throws Exception {
        return login_raw(email, password);
    }


    public RequestBuilder changeEmail(String code, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getChangeEmailUrl())
                .param("code", code)
//                .param("id", targetId.toString())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder changeEmailWithLink(String link, String token) throws Exception {
        return post(link)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }


    public RequestBuilder requestEmailChange(Serializable targetId, String token, Object requestNewEmailDto) throws Exception {
        return post(getController().getAuthProperties().getController().getRequestEmailChangeUrl())
                .param("id", targetId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(requestNewEmailDto));
    }

    public MailData requestEmailChange2xx(Serializable targetId, String token, Object requestNewEmailDto) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getRequestEmailChangeUrl())
                .param("id", targetId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(serialize(requestNewEmailDto)))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder changePassword(Serializable id, String token, Object changePasswordDto) throws Exception {
        return post(getController().getAuthProperties().getController().getChangePasswordUrl())
                .param("id", id.toString())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(changePasswordDto));
    }

    public RequestBuilder forgotPassword(String email) throws Exception {
        return post(getController().getAuthProperties().getController().getForgotPasswordUrl())
                .param("email", email)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MailData forgotPassword2xx(String email) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getForgotPasswordUrl())
                .param("email", email)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public RequestBuilder resetPassword(ResetPasswordView resetPasswordView, String code) throws Exception {
        return post(getController().getAuthProperties().getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", code)
                .content("password="+resetPasswordView.getPassword()+"&matchPassword="+resetPasswordView.getMatchPassword());
    }

    public RequestBuilder getResetPasswordView(String link) throws Exception {
        return get(link);
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(serialize(resetPasswordDto)));
    }

    public RequestBuilder resetPassword(String url, Object resetPasswordDto, String code) throws Exception {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("code", code)
                .content(serialize(resetPasswordDto));
    }

    public RequestBuilder fetchNewToken(String token) throws Exception {
        return post(getController().getAuthProperties().getController().getNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder fetchNewToken(String token, String email) throws Exception {
        return post(getController().getAuthProperties().getController().getNewAuthTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("email", email)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public String fetchNewToken2xx(String token, String email) throws Exception {
        return deserialize(mvc.perform(fetchNewToken(token, email))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ResponseToken.class).getToken();
    }

    public String fetchNewToken2xx(String token) throws Exception {
        return deserialize(mvc.perform(fetchNewToken(token))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ResponseToken.class).getToken();
    }

    protected MockHttpServletRequestBuilder login_raw(String email, String password) {
        return post(getController().getAuthProperties().getController().getLoginUrl())
                .param("username", email)
                .param("password", password)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder fetchByEmail(String email) throws Exception {
        return post(getController().getAuthProperties().getController().getFetchByEmailUrl())
                .param("email", email)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public String login2xx(String email, String password) throws Exception {
        return getMvc().perform(login_raw(email, password))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    public RequestBuilder verifyEmail(String code) throws Exception {
        return post(getController().getAuthProperties().getController().getVerifyUserUrl())
//                .param("id", id.toString())
                .param("code", code)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public RequestBuilder verifyEmailWithLink(String link) throws Exception {
        return post(link)
//                .param("id", id.toString())
//                .param("code", code)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED);
    }

    public MailData verifyMailWasSend() {
        ArgumentCaptor<MailData> captor = ArgumentCaptor.forClass(MailData.class);
        verify(mailSenderMock, times(1)).send(captor.capture());
        MailData sentData = captor.getValue();
        Mockito.reset(mailSenderMock);
        return sentData;
    }


    public RequestBuilder resendVerificationEmail(Serializable id, String token) throws Exception {
        return post(getController().getAuthProperties().getController().getResendVerificationEmailUrl())
                .param("id", id.toString())
                .header(HttpHeaders.AUTHORIZATION, token);
    }

    public MailData resendVerificationEmail2xx(Serializable id, String token) throws Exception {
        mvc.perform(post(getController().getAuthProperties().getController().getResendVerificationEmailUrl())
                .param("id", id.toString())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful());
        return verifyMailWasSend();
    }

    public String login2xx(AbstractUser user) throws Exception {
        return login2xx(user.getEmail(), user.getPassword());
    }

    public void mockLogin(AbstractUser user) {
        rapidSecurityContext.login(authenticatedPrincipalFactory.create(user));
    }

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Autowired
    public void setRapidSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext) {
        this.rapidSecurityContext = rapidSecurityContext;
    }

    public static class ResponseToken {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }


    // todo add more methods there for each endpoint
}
