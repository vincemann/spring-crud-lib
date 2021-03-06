package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

@Getter
public class AbstractControllerTestTemplate<C extends GenericCrudController> {

   
    protected C controller;
    protected MockMvc mvc;

    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }

    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return (E) getController().getDtoMapper().mapToEntity(dto, getController().getEntityClass());
    }

    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  String serialize(Object o) throws JsonProcessingException {
        return getController().getJsonMapper().writeDto(o);
    }


    public  <Dto> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception {
        return deserialize(mvcResult.getResponse().getContentAsString(), dtoClass);
    }

    public void setController(C controller) {
        this.controller = controller;
    }

    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }
}
