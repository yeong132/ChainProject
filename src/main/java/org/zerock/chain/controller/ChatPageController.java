package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.service.EmployeeService;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
public class ChatPageController {

    @Autowired
    private EmployeeService employeeService;

    // 채팅 조직도 데이터
    @GetMapping("/chatting")
    public String getAllEmployees(Model model) {
        Map<String, List<EmployeeDTO>> departmentMap = employeeService.getAllEmployeesGroupedByDepartment();
        model.addAttribute("departmentMap", departmentMap);
        return "chatting"; // templates/chatting.html 파일을 서빙
    }


    @GetMapping("/chatting/ws") // 웹소켓
    public String chatWebSocket() {
        return "chatting";
    }

}
