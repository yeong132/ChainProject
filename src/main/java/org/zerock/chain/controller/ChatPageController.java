package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@Controller
public class ChatPageController {

//    @Autowired
//    private EmployeeService employeeService;

    @GetMapping("/chatting")
    public String chat() {
        return "chatting"; // templates/chatting.html 파일을 서빙
    }

    // 채팅 조직도 데이터
//    @GetMapping("/chatting")
//    public String getAllEmployees(Model model) {
//        Map<String, List<EmployeeDTO>> departmentMap = employeeService.getAllEmployeesGroupedByDepartment();
//        model.addAttribute("departmentMap", departmentMap);
//        return "chatting"; // templates/chatting.html 파일을 서빙
//    }


//    @GetMapping("/chatting/ws") // 웹소켓
//    public String chatWebSocket() {
//        return "chatting";
//    }

}
