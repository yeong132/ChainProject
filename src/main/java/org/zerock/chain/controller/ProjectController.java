package org.zerock.chain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        List<ProjectDTO> temporaryProjects = projectService.getTemporaryProjects(); // 임시 보관 프로젝트 조회 추가
        model.addAttribute("projects", projects);
        model.addAttribute("temporaryProjects", temporaryProjects); // 임시 보관 프로젝트 모델에 추가
        return "project/list";
    }

    // 즐겨찾기 상태 업로드
    @PostMapping("/toggleFavorite")
    public String toggleFavorite(@RequestParam("projectNo") Long projectNo, @RequestParam("projectFavorite") Boolean projectFavorite, RedirectAttributes redirectAttributes) {
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
        model.addAttribute("project", projectDTO);
        return "project/modify"; // 수정 페이지의 뷰 이름으로 변경
    }

    // 프로젝트 수정 처리
    @PostMapping("/modify/{projectNo}")
    public String modifyPOST(@PathVariable Long projectNo, @Valid ProjectRequestDTO projectRequestDTO, BindingResult bindingResult, @RequestParam("isTemporary") boolean isTemporary) {
        projectRequestDTO.setIsTemporary(isTemporary); // 임시 보관 여부 설정
        projectService.updateProject(projectNo, projectRequestDTO);
        return "redirect:/project/list"; // 수정 후 list 페이지로 리다이렉트
    }


    // 프로젝트 삭제 처리
    @DeleteMapping("/delete/{projectNo}")
    @ResponseBody
    public String deleteProject(@PathVariable Long projectNo) {
        projectService.deleteProject(projectNo);
        return "redirect:/project/list";
    }

    // 새 프로젝트 생성 조회
    @GetMapping("/register")
    public void registerGET() {
    }
    // 새 프로젝트 등록 처리 (C ---> S 요청 ---> S는 R에게 요청 insert )  // 프로젝트 진행도 및 등록 처리
    @PostMapping("/register")
    public String registerAndProgress(@Valid @ModelAttribute ProjectDTO projectDTO, BindingResult bindingResult, Model model, @RequestParam("isTemporary") boolean isTemporary) {
        if (bindingResult.hasErrors()) {
            return "project/register"; // 다시 입력 폼으로 이동
        }
        projectDTO.setIsTemporary(isTemporary); // 임시 보관 여부 설정
        Long projectNo = projectService.register(projectDTO);
        projectService.updateProjectProgress(projectNo, projectDTO.getProjectProgress());
        model.addAttribute("projectNo", projectNo);
        return "redirect:/project/list";
    }

}
