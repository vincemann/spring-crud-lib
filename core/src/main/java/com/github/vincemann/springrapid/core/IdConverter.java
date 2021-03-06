package com.github.vincemann.springrapid.core;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;

import java.io.Serializable;

@FunctionalInterface
@ServiceComponent
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
}
