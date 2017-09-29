package org.bdlions.util;


import com.bdlions.dto.Credential;
import com.bdlions.util.ACTION;
import org.bdlions.dto.EntityUser;
import org.bdlions.manager.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bdlions.roles.USER_ROLE;
import org.bdlions.session.db.IDBUserProvider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alamgir
 */
public class DBUserProvider implements IDBUserProvider{

    private Map<String, Credential> users = new HashMap();
    @Override
    public Credential getLoggedInUserByUserName(String userName){
        return users.get(userName);
    }
    
    @Override
    public Credential getUser(Credential credential) {
        if( !StringUtils.isNullOrEmpty(credential.getUserName()) && !StringUtils.isNullOrEmpty(credential.getPassword()) ){
            String userName = credential.getUserName();
            String password = credential.getPassword();
            User user = new User();
            EntityUser entityUser = user.getUserByCredential(userName, password);
            if(entityUser != null)
            {
                credential.setAppType(100);
                credential.setFirstName(entityUser.getFirstName());
                credential.setLastName(entityUser.getLastName());
                credential.setUserId(entityUser.getId());
                users.put(credential.getUserName(), credential);
                return credential;
            }
            return null;            
        }
        return null;
    }

    @Override
    public Set<String> getUserRoles(String userName) {
        return new HashSet<>(Arrays.asList(USER_ROLE.ADMIN.name(), USER_ROLE.FLAT_OWNER.name(), USER_ROLE.FLAT_TENANTS.name()));
    }

    @Override
    public Collection<String> getPermissionsByRole(Set<String> roles) {
        ArrayList<String> permissions = new ArrayList<>();
        for (String role : roles) {
            if(role.equals(USER_ROLE.ADMIN.name())){
                permissions.add(ACTION.SIGN_IN.name());
                permissions.add(ACTION.SIGN_OUT.name());
                permissions.add(ACTION.FETCH_USER_INFO.name());
            }
        }
        return permissions;
    }

    @Override
    public Collection<String> getUserPermissions(String userName) {
        return getPermissionsByRole(getUserRoles(userName));
    }
    
}
