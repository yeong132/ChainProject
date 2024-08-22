package org.zerock.chain.ksh.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.chain.imjongha.dto.EmployeeDTO;
import org.zerock.chain.ksh.service.ChatUserService;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

//    @MessageMapping("/user.addUser") // 클라이언트가 /user.addUser로 메시지 보낼때 호출
//    @SendTo("/topic/public") // 반환한 employeeDTO값을 받음
//    public Long addUser(@Payload Long empNo) {
////        chatUserService.saveEmployee(employee);
//        log.info("디티오ㅗㅗㅗㅗㅗㅗㅗㅗㅗReceived EmployeeDTO: {}", empNo);
//        return empNo; // 받은 사원 데이터 그대로 반환
//    }

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    public EmployeeDTO addUser(@Payload Long empNo) {
        // 사원 번호로 사원 정보 조회
        EmployeeDTO employeeDTO = chatUserService.findEmployeeByEmpNo(empNo);

        if (employeeDTO != null) {
            log.info("디티오ㅗㅗㅗㅗㅗRetrieved EmployeeDTO: {}", employeeDTO);
            return employeeDTO;
        } else {
            log.warn("사원ㄴㄴㄴEmployee not found for empNo: {}", empNo);
            return null; // 혹은 적절한 처리
        }
    }

    @MessageMapping("/user.disconnectUser") // 연결 끊김
    @SendTo("/topic/public")
    public Long disconnectUser(@Payload Long empNo) {
//        chatUserService.disconnect(empNo);
        return empNo;
    }

    // 대화 중인 사용자만 반환(채팅방 호출)
    @GetMapping("/chat/activeUsers")
//    public ResponseEntity<List<Employee>> findActiveChatUsers(@RequestParam Long empNo) {
//        List<Employee> activeUsers = chatUserService.findActiveChatUsers(empNo);
//        return ResponseEntity.ok(activeUsers);
//    }
//    public ResponseEntity<List<Long>> findActiveChatUsers(@RequestParam Long empNo) {
//        List<Long> activeUsers = chatUserService.findActiveChatUsers(empNo);
//        return ResponseEntity.ok(activeUsers);
//    }
    public ResponseEntity<List<EmployeeDTO>> findActiveChatUsers(@RequestParam Long empNo) {
        List<EmployeeDTO> activeUsers = chatUserService.findActiveChatUsers(empNo);
        return ResponseEntity.ok(activeUsers);
    }
}
