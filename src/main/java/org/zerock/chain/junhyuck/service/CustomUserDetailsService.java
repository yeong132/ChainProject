package org.zerock.chain.junhyuck.service;

import org.zerock.chain.imjongha.model.Rank;
import org.zerock.chain.junhyuck.model.Signup;
import org.zerock.chain.junhyuck.repository.SignupRepository;
import org.zerock.chain.imjongha.repository.RankRepository;
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

    @Autowired
    private RankRepository rankRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Long empNo = Long.valueOf(username);
            Signup user = signupRepository.findByEmpNo(empNo)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with empNo: " + empNo));

            // rankNo를 통해 rankName 조회
            Rank rank = rankRepository.findById(user.getRankNo())
                    .orElseThrow(() -> new UsernameNotFoundException("Rank not found with rankNo: " + user.getRankNo()));
            String rankName = rank.getRankName();

            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            // 사원이름과 직급을 CustomUserDetails에 전달
            return new CustomUserDetails(
                    user.getEmpNo().toString(),
                    user.getPassword(),
                    authorities,
                    user.getEmpNo(),
                    user.getFirstName(),
                    user.getLastName(),
                    rankName
            );
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Username must be a numeric value representing empNo.");
        }
    }
}
