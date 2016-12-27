package com.wms.sercurity;

import com.google.common.collect.Lists;
import com.wms.dto.ActionMenuDTO;
import com.wms.dto.CustomerDTO;
import com.wms.dto.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by duyot on 11/18/2016.
 */
public class WMSUserDetails implements UserDetails,CredentialsContainer {
    private User user;
    private Set<GrantedAuthority> lstAuthorities;
    private List<ActionMenuDTO> lstMenu;

    public WMSUserDetails(User user) {
        this.user = user;
    }

    public WMSUserDetails(User user, List<ActionMenuDTO> lstMenu) {
        this.user = user;
        this.lstAuthorities = getLstAuthorities(user.getRoleCode());
        this.lstMenu = lstMenu;
    }

    public Set<GrantedAuthority> getLstAuthorities(String roleName){
        Set<GrantedAuthority> authorities = new HashSet<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+roleName);
        authorities.add(grantedAuthority);
        return authorities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public List<ActionMenuDTO> getLstMenu() {
        return lstMenu;
    }

    public void setLstMenu(List<ActionMenuDTO> lstMenu) {
        this.lstMenu = lstMenu;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return lstAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.user.setPassword(null);
    }
}
