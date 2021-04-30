package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
@Slf4j
@ServiceConfig
@AutoConfigureAfter(RapidSecurityProxyAutoConfiguration.class)
public class RapidDefaultSecurityExtensionAutoConfiguration {

    @Autowired
    ApplicationContext context;

    @ConditionalOnProperty(prefix = "rapid-acl", name = "defaultAclChecks")
    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Secured List<CrudService> securityProxies){
        if (aclProperties.isDefaultAclChecks()) {
            for (CrudService securityProxy : securityProxies) {
                ServiceExtensionProxy proxy = ProxyUtils.getExtensionProxy(securityProxy);
                if (proxy.getDisableDefaultExtensions()){
                    log.debug("Default security extensions disabled for proxy: " + proxy);
                    continue;
                }
                log.debug("Adding Default security extensions for proxy: " + proxy);
                proxy.addExtension(
                        (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("simpleSecurityExtension")
                );
            }
        }
    }
}