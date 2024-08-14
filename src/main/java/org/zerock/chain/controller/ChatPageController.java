package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.service.EmployeeService;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
//@RequestMapping("/chatting")
public class ChatPageController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/chatting")
    public String getAllEmployees(Model model) {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "chatting"; // templates/chatting.html 파일을 서빙
    }

    @GetMapping("/chatting/ws") // 웹소켓
    public String chatWebSocket() {
        return "chatting";
    }


}
