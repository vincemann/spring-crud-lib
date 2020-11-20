package com.github.vincemann.springrapid.core.slicing.components;


import com.github.vincemann.springrapid.core.RapidProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile(RapidProfiles.WEB)
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebComponent {
}
