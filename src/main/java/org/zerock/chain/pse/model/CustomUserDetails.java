package org.zerock.chain.pse.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Long empNo;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long empNo) {
        super(username, password, authorities);
        this.empNo = empNo;
    }

    public Long getEmpNo() {
        return empNo;
    }
}
