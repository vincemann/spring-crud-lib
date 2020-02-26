package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import org.springframework.stereotype.Controller;

@Controller
public class ExampleController
        extends SpringAdapterDtoCrudController<ExampleEntity,Long>
{
    public ExampleController(DtoMappingContext dtoMappingContext) {
        super(dtoMappingContext);
    }
}