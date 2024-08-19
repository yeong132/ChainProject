package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.ChartDTO;
import org.zerock.chain.dto.ChartRequestDTO;
import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.service.ChartService;
import org.zerock.chain.service.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/chart")
@Log4j2
public class ChartController {

    private final ChartService chartService;
    private final ModelMapper modelMapper;
    private final ProjectService projectService;

    @Autowired
    public ChartController(ChartService chartService, ModelMapper modelMapper, ProjectService projectService) {
        this.chartService = chartService;
        this.modelMapper = modelMapper;
        this.projectService = projectService;
    }

    // 차트 메인 페이지
    @GetMapping("/main")
    public String showMainPage(Model model) {
        return "chart/main";
    }

    // OKR 차트 페이지: 전체 목록 조회
    @GetMapping("/OKR")
    public String showOkrCharts(Model model) {
        List<ChartDTO> charts = chartService.getAllCharts();
        model.addAttribute("charts", charts);
        return "chart/OKR";
    }

    // 차트 생성 등록
    @PostMapping("/create")
    public String createChart(ChartRequestDTO chartRequestDTO) {
        chartService.createChart(chartRequestDTO);
        log.info("Chart created with name: {}", chartRequestDTO.getChartName());
        return "redirect:/chart/OKR";
    }


    // 차트 상세 정보 조회 및 수정 모달 표시
    @GetMapping("/detail/{chartNo}")
    @ResponseBody
    public ResponseEntity<ChartDTO> getChartDetail(@PathVariable Long chartNo) {
        ChartDTO chartDTO = chartService.getChartById(chartNo);
        if (chartDTO == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found 반환
        }
        return ResponseEntity.ok(chartDTO); // 200 OK와 함께 데이터 반환
    }


    // 차트 수정 등록
    @PostMapping("/update")
    public String updateChart(ChartRequestDTO chartRequestDTO) {
        chartService.updateChart(chartRequestDTO.getChartNo(), chartRequestDTO);
        return "redirect:/chart/OKR";
    }

    // 차트 삭제
    @PostMapping("/delete/{chartNo}")
    public String deleteChart(@RequestParam Long chartNo) {
        chartService.deleteChart(chartNo);
        return "redirect:/chart/OKR";
    }

    // 프로젝트 차트 조회
    @GetMapping("/project")
    public String showProjectCharts(Model model) {
        // 모든 프로젝트를 가져와서 모델에 추가
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();
        model.addAttribute("projects", projectDTOList);
        return "chart/project";
    }
    @GetMapping("/project/details/{projectNo}")
    public ResponseEntity<ProjectDTO> getProjectDetails(@PathVariable Long projectNo) {
        ProjectDTO project = projectService.getProjectById(projectNo);
        return ResponseEntity.ok(project);
    }
}
