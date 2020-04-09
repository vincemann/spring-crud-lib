package io.github.vincemann.springrapid.acl.config;

import io.github.vincemann.springrapid.acl.framework.NoModSecurityCheckAclAuthorizationStrategy;
import io.github.vincemann.springrapid.acl.framework.SophisticatedPermissionGrantingStrategy;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@ServiceConfig
@Slf4j
@ConditionalOnClass(DataSource.class)
public class AclAutoConfiguration {

    public AclAutoConfiguration() {
        log.debug("AclAutoConfiguration loaded");
    }

    @Autowired
    DataSource dataSource;

    @Value("${rapid.acl.adminRole:ROLE_ADMIN}")
    String adminRole;

    @ConditionalOnMissingClass
    @Bean
    public EhCacheBasedAclCache aclCache() {
        return new EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @ConditionalOnMissingClass
    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @ConditionalOnMissingClass
    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        //for unit tests, es soll nicht immer wieder neuer manager erstellt werden, der dann cacheFactory mit selben name kreiert, was die rules violated
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }

    @ConditionalOnMissingClass
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new SophisticatedPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @ConditionalOnMissingClass
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        //admin is allowed to change all acl db tables, but he is not automatically allowed to do anything acl restricted bc of this statement
        //return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(AuthorityName.ROLE_ADMIN.toString()));
        return new NoModSecurityCheckAclAuthorizationStrategy(
                new SimpleGrantedAuthority(adminRole)
        );
    }

    @ConditionalOnMissingClass
    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }

    @ConditionalOnMissingClass
    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    @ConditionalOnMissingClass
    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }

    @ConditionalOnMissingClass
    @Bean
    public PermissionEvaluator permissionEvaluator(){
        return new AclPermissionEvaluator(aclService());
    }

}

