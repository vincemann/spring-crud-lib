package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repositories.UserRepository;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

//@ServiceComponent
//@Primary
public class MyUserService extends AbstractUserService<User, Long, UserRepository> {

	@Override
    public User newUser() {
        return new User();
    }

	@Override
	public Long toId(String id) {
		return Long.valueOf(id);
	}

	@Override
	public Class<?> getTargetClass() {
		return MyUserService.class;
	}

	@Override
	public User newAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.newAdmin(admin);
		createdAdmin.setName(admin.getEmail()+"name");
		return createdAdmin;
	}
}