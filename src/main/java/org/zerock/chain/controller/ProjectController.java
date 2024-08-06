package org.zerock.chain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.dto.ProjectRequestDTO;
import org.zerock.chain.service.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/project")
@Log4j2
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 프로젝트 전체 목록 조회
    @GetMapping("/list")
    public String listGET(Model model) {
        List<ProjectDTO> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "project/list";
    }

    // 즐겨찾기 상태 업로드
    @PostMapping("/toggleFavorite")
    public String toggleFavorite(@RequestParam("projectNo") Long projectNo, @RequestParam("projectFavorite") Boolean projectFavorite) {
        projectService.setProjectFavorite(projectNo, projectFavorite);
        return "redirect:/project/list";
    }

    // 프로젝트 개별 상세 조회
    @GetMapping("/detail/{projectNo}")
    public String readGET(@PathVariable Long projectNo, ProjectRequestDTO projectRequestDTO, Model model) {
        ProjectDTO projectDTO = projectService.getProjectById(projectNo);
        model.addAttribute("project", projectDTO);
        return "project/detail";
    }

    // 프로젝트 수정 페이지 조회
    @GetMapping("/modify/{projectNo}")
    public String modifyGET(@PathVariable Long projectNo, Model model) {
        ProjectDTO projectDTO = projectService.getProjectById(projectNo);
        log.info(projectDTO.toString());
        model.addAttribute("project", projectDTO);
        return "project/modify";
    }

    // 프로젝트 수정 처리
    @PostMapping("/modify/{projectNo}")
    public String modifyPOST(@PathVariable Long projectNo, @Valid ProjectRequestDTO projectRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "project/modify";
        }
        projectService.updateProject(projectNo, projectRequestDTO);
        return "redirect:/project/detail/" + projectNo;
    }

    
    //  임시보관 프로젝트 개별 상세  확인 페이지
    @GetMapping("/임시보관detail")
    public String detail2(Model model) {
        return "project/임시보관detail";
    }


    // 새 프로젝트 생성 페이지
    @GetMapping("/register")
    public void registerGET() {
    }

    // 새 프로젝트 등록 처리 (C ---> S 요청 ---> S는 R에게 요청 insert )  // 프로젝트 진행도 및 등록 처리
    @PostMapping("/register")
    public String registerAndProgress(@Valid @ModelAttribute ProjectDTO projectDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "project/register"; // 다시 입력 폼으로 이동
        }
        Long projectNo = projectService.register(projectDTO);
        projectService.updateProjectProgress(projectNo, projectDTO.getProjectProgress());
        model.addAttribute("projectNo", projectNo);
        return "redirect:/project/list";
    }

    //  프로젝트 차트 페이지
    @GetMapping("/chart")
    public String charPage(Model model) {
        return "project/chart";
    }

}
