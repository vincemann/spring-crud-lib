package com.github.vincemann.springrapid.authdemo.repositories;

import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}