package org.zerock.chain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.chain.DTO.ProjectDTO;
import org.zerock.chain.Service.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/project")
@Log4j2
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 프로젝트 목록 페이지
    @GetMapping("/list")
    public String listGET(Model model) {
        List<ProjectDTO> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "project/list";
    }

    // 프로젝트 개별 상세  확인 페이지
    @GetMapping("/detail")
    public String detail(Model model) {
        return "project/detail";
    }

    //  임시보관 프로젝트 개별 상세  확인 페이지
    @GetMapping("/임시보관detail")
    public String detail2(Model model) {
        return "project/임시보관detail";
    }

    // 프로젝트 수정 페이지
    @GetMapping("/modify")
    public String modify(Model model) {
        return "project/modify";
    }

    // 새 프로젝트 생성 페이지
    @GetMapping("/register")
    public void registerGET() {
    }

    // 새 프로젝트 등록 처리 (C ---> S 요청 ---> S는 R에게 요청 insert )
    @PostMapping("/register")
    public String registerPOST(@Valid @ModelAttribute ProjectDTO projectDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info("Validation errors while submitting form: " + bindingResult.getAllErrors());
            model.addAttribute("projectDTO", projectDTO);
            return "project/register"; // 다시 입력 폼으로 이동
        }
        log.info("project Post register: " + projectDTO);
        Long projectNo = projectService.register(projectDTO);
        model.addAttribute("projectNo", projectNo);
        log.info("++++++++++++++++++++++++++++++++++++++++++++++++"+ projectNo);
        log.info("++++++++++++++++++++++++++++++++++++++++++++++++"+ projectDTO);
        return "redirect:/project/list";
    }

    //  프로젝트 차트 페이지
    @GetMapping("/chart")
    public String charPage(Model model) {
        return "project/chart";
    }

}
