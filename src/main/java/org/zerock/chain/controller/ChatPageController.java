package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.model.Department;
import org.zerock.chain.repository.DepartmentRepository;
import org.zerock.chain.service.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
public class ChatPageController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/chatting")
    public String chat (Model model) {
        // 모든 부서를 가져옴
        List<Department> departments = departmentRepository.findAll();

        // 부서별로 사원들을 그룹화하여 저장할 맵
        Map<String, List<EmployeeDTO>> departmentMap = new HashMap<>();

        // 각 부서에 속한 사원들을 가져와서 맵에 추가
        for (Department department : departments) {
            List<EmployeeDTO> employees = employeeService.getEmployeesByDepartmentId(department.getDmpNo());
            departmentMap.put(department.getDmpName(), employees);
        }

        // 모델에 부서명과 사원 데이터를 추가
        model.addAttribute("departmentMap", departmentMap);

        return "chatting";
    }
}
