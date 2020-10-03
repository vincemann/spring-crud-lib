package com.github.vincemann.springlemon.demo.services;

import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.demo.repositories.UserRepository;
import com.github.vincemann.springlemon.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.context.annotation.Primary;

@ServiceComponent
@Unsecured
@Primary
public class MyService extends AbstractUserService<User, Long, UserRepository> {

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
		return MyService.class;
	}

}