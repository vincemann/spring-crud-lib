package io.github.vincemann.generic.crud.lib.proxy.invocationHandler;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.abs.MethodBlacklistingCrudServiceDynamicInvocationHandler;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class ForceEagerFetchCrudServiceDynamicInvocationHandler
       extends MethodBlacklistingCrudServiceDynamicInvocationHandler<IdentifiableEntity<Serializable>, Serializable> {
    //todo implement applicationcontext aware and get eagerfetchUtil bean like that instead of constructor?

    private HibernateForceEagerFetchUtil eagerFetchUtil;



    public ForceEagerFetchCrudServiceDynamicInvocationHandler(CrudService service, HibernateForceEagerFetchUtil eagerFetchUtil, String... omittedMethods) {
        super(service,omittedMethods);
        this.eagerFetchUtil = eagerFetchUtil;

    }

    @Override
    protected Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable {
        try {
            if(method.getReturnType().equals(Void.TYPE)){
                //no return value -> we dont have do eager fetch anything
                return getMethods().get(method.getName()).invoke(getService(),args);
            }
            if (method.getReturnType().equals(Optional.class)) {
                return eagerFetchUtil.runInTransactionAndFetchEagerly_OptionalValue(() -> {
                    return (Optional<?>) getMethods().get(method.getName()).invoke(getService(), args);
                });
            } else {
                return eagerFetchUtil.runInTransactionAndFetchEagerly(() -> {
                    return getMethods().get(method.getName()).invoke(getService(), args);
                });
            }
        }catch (InvocationTargetException e){
            throw e.getCause();
        }
    }

}
