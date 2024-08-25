package org.zerock.chain.junhyuck.service;

import org.zerock.chain.junhyuck.model.Signup;
import org.zerock.chain.junhyuck.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.chain.pse.model.CustomUserDetails;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SignupRepository signupRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Long empNo = Long.valueOf(username);
            Signup user = signupRepository.findByEmpNo(empNo)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with empNo: " + empNo));

            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return new CustomUserDetails(user.getEmpNo().toString(), user.getPassword(), authorities, user.getEmpNo());
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Username must be a numeric value representing empNo.");
        }
    }
}
