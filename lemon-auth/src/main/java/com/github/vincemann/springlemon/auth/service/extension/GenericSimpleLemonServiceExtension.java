package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.service.SimpleLemonService;
import com.github.vincemann.springrapid.core.proxy.GenericSimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface GenericSimpleLemonServiceExtension<S extends SimpleLemonService<U,Id>,U extends AbstractUser<Id>, Id extends Serializable>
        extends SimpleLemonService<U,Id>, GenericSimpleCrudServiceExtension<S,U,Id>
{


    @Override
    default Map<String, Object> getContext(Optional<Long> expirationMillis, HttpServletResponse response) {
        return getNext().getContext(expirationMillis,response);
    }

    @Override
    default U signup(U user) throws BadEntityException {
        return getNext().signup(user);
    }

    @Override
    default void resendVerificationMail(U user) {
        getNext().resendVerificationMail(user);
    }

    @Override
    default U findByEmail(@Valid @Email @NotBlank String email) {
        return getNext().findByEmail(email);
    }

    @Override
    default U verifyUser(Id userId, String verificationCode) {
        return getNext().verifyUser(userId,verificationCode);
    }

    @Override
    default void forgotPassword(@Valid @Email @NotBlank String email) {
        getNext().forgotPassword(email);
    }

    @Override
    default U resetPassword(@Valid ResetPasswordForm form) {
        return getNext().resetPassword(form);
    }

    @Override
    default String changePassword(U user, @Valid ChangePasswordForm changePasswordForm) {
        return getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(Id userId, @Valid RequestEmailChangeForm emailChangeForm) {
        getNext().requestEmailChange(userId,emailChangeForm);
    }

    @Override
    default U changeEmail(Id userId, @Valid @NotBlank String changeEmailCode) {
        return getNext().changeEmail(userId,changeEmailCode);
    }

    @Override
    default String fetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername) {
        return getNext().fetchNewToken(expirationMillis,optionalUsername);
    }

    @Override
    default Map<String, String> fetchFullToken(String authHeader) {
        return getNext().fetchFullToken(authHeader);
    }

    @Override
    default void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
    }

    @Override
    default Id toId(String id) {
        return getNext().toId(id);
    }

    @Override
    default void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis) {
        getNext().addAuthHeader(response,username,expirationMillis);
    }

    @Override
    default U update(U entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }
}