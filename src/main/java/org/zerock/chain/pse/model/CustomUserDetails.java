package org.zerock.chain.pse.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Long empNo;
    private final String firstName;
    private final String lastName;
    private final String rankName;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long empNo, String firstName, String lastName, String rankName) {
        super(username, password, authorities);
        this.empNo = empNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rankName = rankName;
    }

    public Long getEmpNo() {
        return empNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRankName() {
        return rankName;
    }
}
