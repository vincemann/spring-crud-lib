package io.github.vincemann.generic.crud.lib.config.layers.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Profile("serviceTest")
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceTestComponent {
}
