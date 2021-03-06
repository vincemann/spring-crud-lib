package com.github.vincemann.springrapid.entityrelationship.exception;

public class UnknownEntityTypeException extends EntityRelationHandlingException {

    public UnknownEntityTypeException(Class entityClazz, Class actualType){
        super("DstEntity from SrcEntity "+entityClazz.getSimpleName()+" was of unknown Type: " + actualType.getSimpleName());
    }
}
