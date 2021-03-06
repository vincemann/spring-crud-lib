package com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation;

import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirParentId
public @interface BiDirParentId {
    /**
     * Type of Parent which belongs to the annotated parent id
     * @return
     */
    Class<? extends BiDirParent> value();
}
