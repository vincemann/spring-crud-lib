package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class AbstractAclPlugin extends CrudServicePlugin {
    private LocalPermissionService permissionService;
    private MutableAclService mutableAclService;

    protected void saveFullPermissionForAdminOver(IdentifiableEntity<Serializable> entity){
        getPermissionService().addPermissionForAuthorityOver(entity, BasePermission.ADMINISTRATION, Role.ADMIN);
    }

    protected void savePermissionForAuthenticatedOver(IdentifiableEntity<Serializable> entity, Permission permission){
        String own = findAuthenticatedUsername();
        getPermissionService().addPermissionForUserOver(entity, permission,own);
    }

    protected String findAuthenticatedUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Nicht auslagern. MutableAclService macht das intern auch so -> use @MockUser(username="testUser") in tests
        if(authentication==null){
            throw new IllegalArgumentException("Authentication required");
        }
        return authentication.getName();
    }
}