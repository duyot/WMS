package com.wms.sercurity;

import com.wms.dto.ActionMenuDTO;
import com.wms.dto.CatUserDTO;
import com.wms.utils.DataUtil;
import java.util.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by duyot on 11/18/2016.
 */
public class WMSUserDetails implements UserDetails, CredentialsContainer {
    private CatUserDTO catUserDTO;
    private Set<GrantedAuthority> lstAuthorities;
    private List<ActionMenuDTO> lstMenu;

    public WMSUserDetails(CatUserDTO catUserDTO, List<ActionMenuDTO> lstMenu) {
        this.catUserDTO = catUserDTO;
        this.lstAuthorities = getLstAuthorities(catUserDTO.getRoleId());
        this.lstMenu = lstMenu;
    }

    public Set<GrantedAuthority> getLstAuthorities(String roleName) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        //todo
        //GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+roleName);
        //authorities.add(grantedAuthority);
        return authorities;
    }

    public CatUserDTO getCatUserDTO() {
        return catUserDTO;
    }

    public void setCatUserDTO(CatUserDTO catUserDTO) {
        this.catUserDTO = catUserDTO;
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
        return catUserDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return catUserDTO.getCode();
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
        this.catUserDTO.setPassword(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WMSUserDetails that = (WMSUserDetails) o;
        if (DataUtil.isNullOrEmpty(catUserDTO.getId())) {
            return false;
        }
        if (that.getCatUserDTO() == null) {
            return false;
        }
        return catUserDTO.getId().equalsIgnoreCase(that.getCatUserDTO().getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(catUserDTO.getId());
    }
}
