package com.github.vincemann.springrapid.entityrelationship.model.child.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see UniDirChild
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirChildCollection {


    /**
     *
     * @return generic type of annotated collection
     */
    Class value();
}
