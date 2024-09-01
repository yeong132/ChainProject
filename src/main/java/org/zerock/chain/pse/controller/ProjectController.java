package org.zerock.chain.pse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.chain.pse.dto.ProjectDTO;
import org.zerock.chain.pse.dto.ProjectRequestDTO;
import org.zerock.chain.pse.service.ProjectService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project")
@Log4j2
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;  // 프로젝트 서비스 계층 의존성 주입

    // 프로젝트 전체 목록 조회
    @GetMapping("/history")
    public String historyGET(Model model) {
        // 전체 프로젝트를 조회한 후 업로드 날짜(uploadDate) 기준으로 내림차순 정렬
        List<ProjectDTO> projects = projectService.getAllProjects()
                .stream()
                .sorted(Comparator.comparing(ProjectDTO::getUploadDate).reversed())
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);  // 정렬된 프로젝트를 모델에 추가
        return "project/history";  // 프로젝트 히스토리 페이지로 이동
    }

    // 프로젝트 목록 조회
    @GetMapping("/list")
    public String listGET(Model model) {
        // 전체 프로젝트를 조회한 후 업로드 날짜(uploadDate) 기준으로 내림차순 정렬
        List<ProjectDTO> projects = projectService.getAllProjects()
                .stream()
                .sorted(Comparator.comparing(ProjectDTO::getUploadDate).reversed())
                .collect(Collectors.toList());

        // 임시 보관 프로젝트를 조회한 후 업로드 날짜(uploadDate) 기준으로 내림차순 정렬
        List<ProjectDTO> temporaryProjects = projectService.getTemporaryProjects()
                .stream()
                .sorted(Comparator.comparing(ProjectDTO::getUploadDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("projects", projects);  // 정렬된 프로젝트를 모델에 추가
        model.addAttribute("temporaryProjects", temporaryProjects);  // 정렬된 임시 보관 프로젝트를 모델에 추가
        return "project/list";  // 프로젝트 목록 페이지로 이동
    }

    // 즐겨찾기 상태 업로드
    @PostMapping("/toggleFavorite")
    public String toggleFavorite(@RequestParam("projectNo") Long projectNo, @RequestParam("projectFavorite") Boolean projectFavorite, RedirectAttributes redirectAttributes) {
        projectService.setProjectFavorite(projectNo, projectFavorite);  // 즐겨찾기 상태 업데이트
        return "redirect:/project/list";  // 업데이트 후 프로젝트 목록 페이지로 리다이렉트
    }

    // 프로젝트 개별 상세 조회
    @GetMapping("/detail/{projectNo}")
    public String readGET(@PathVariable Long projectNo, ProjectRequestDTO projectRequestDTO, Model model) {
        ProjectDTO projectDTO = projectService.getProjectById(projectNo);  // 특정 프로젝트 번호로 프로젝트 조회
        model.addAttribute("project", projectDTO);  // 조회된 프로젝트를 모델에 추가
        return "project/detail";  // 프로젝트 상세 페이지로 이동
    }

    // 프로젝트 수정 페이지 조회
    @GetMapping("/modify/{projectNo}")
    public String modifyGET(@PathVariable Long projectNo, Model model) {
        ProjectDTO projectDTO = projectService.getProjectById(projectNo);  // 수정할 프로젝트 조회
        model.addAttribute("project", projectDTO);  // 조회된 프로젝트를 모델에 추가
        return "project/modify";  // 프로젝트 수정 페이지로 이동
    }

    // 프로젝트 수정 처리
    @PostMapping("/modify/{projectNo}")
    public String modifyPOST(@PathVariable Long projectNo, @Valid ProjectRequestDTO projectRequestDTO, BindingResult bindingResult, @RequestParam("isTemporary") boolean isTemporary) {
        // 프로젝트 수정시 기존 사원번호를 유지하도록 설정
        projectRequestDTO.setIsTemporary(isTemporary);  // 임시 보관 여부 설정
        projectService.updateProject(projectNo, projectRequestDTO);  // 프로젝트 수정 처리
        return "redirect:/project/list";  // 수정 후 프로젝트 목록 페이지로 리다이렉트
    }

    // 프로젝트 삭제 처리
    @DeleteMapping("/delete/{projectNo}")
    @ResponseBody
    public String deleteProject(@PathVariable Long projectNo) {
        projectService.deleteProject(projectNo);  // 프로젝트 삭제 처리
        return "redirect:/project/list";  // 삭제 후 프로젝트 목록 페이지로 리다이렉트
    }

    // 새 프로젝트 생성 조회
    @GetMapping("/register")
    public void registerGET() {
        // 프로젝트 등록 페이지로 이동
    }

    // 새 프로젝트 등록 처리
    @PostMapping("/register")
    public String registerAndProgress(@Valid @ModelAttribute ProjectDTO projectDTO, BindingResult bindingResult, Model model, @RequestParam("isTemporary") boolean isTemporary) {
        if (bindingResult.hasErrors()) {
            return "project/register";  // 입력값에 에러가 있을 경우 등록 페이지로 돌아감
        }
        projectDTO.setIsTemporary(isTemporary);  // 임시 보관 여부 설정
        Long projectNo = projectService.register(projectDTO);  // 새로운 프로젝트 등록
        projectService.updateProjectProgress(projectNo, projectDTO.getProjectProgress());  // 프로젝트 진행 상황 업데이트
        model.addAttribute("projectNo", projectNo);  // 등록된 프로젝트 번호를 모델에 추가
        return "redirect:/project/list";  // 등록 후 프로젝트 목록 페이지로 리다이렉트
    }
}
