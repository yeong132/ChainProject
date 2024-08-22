package org.zerock.chain.service;

import org.zerock.chain.model.Signup;
import org.zerock.chain.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SignupRepository signupRepository;

    @Override
    public UserDetails loadUserByUsername(String empNo) throws UsernameNotFoundException {
        // empNo가 숫자인지 확인합니다.
        if (!isNumeric(empNo)) {
            throw new UsernameNotFoundException("Invalid emp_no (not a number): " + empNo);
        }

        // empNo를 long 타입으로 변환하여 사용자를 찾습니다.
        Signup signup = signupRepository.findByEmpNo(Long.parseLong(empNo))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with emp_no: " + empNo));

        // 성과 이름을 결합하여 사용자 이름으로 사용
        String fullName = signup.getLastName() + " " + signup.getFirstName();

        return User.builder()
                .username(fullName)  // 성과 이름을 사용자 이름으로 설정
                .password(signup.getPassword()) // 암호화된 비밀번호
                .roles("USER") // 역할 설정 (필요에 따라 수정 가능)
                .build();
    }

    // 주어진 문자열이 숫자인지 확인하는 메서드
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
