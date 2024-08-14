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
import org.zerock.chain.model.Project;
import org.zerock.chain.service.ChartService;
import org.zerock.chain.service.ProjectService;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chart")
@Log4j2
public class ChartController {

    private final ChartService chartService;
    private final ProjectService projectService;

    @Autowired
    public ChartController(ChartService chartService, ProjectService projectService) {
        this.chartService = chartService;
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
        model.addAttribute("charts", chartService.getAllCharts());
        return "chart/OKR";
    }


    // 차트 생성 등록
    @PostMapping("/create")
    public String createChart(ChartRequestDTO chartRequestDTO) {
        // chartProgress가 null이거나 빈 문자열인 경우 기본값 0을 설정
        if (chartRequestDTO.getChartProgress() == null || chartRequestDTO.getChartProgress().isEmpty()) {
            chartRequestDTO.setChartProgress("0");
        }
        // ChartService를 통해 차트를 생성
        chartService.createChart(chartRequestDTO);
        log.info("Chart created with name: {}", chartRequestDTO.getChartName());
        // 저장 후 OKR 페이지로 리다이렉트
        return "redirect:/chart/OKR";
    }

    // 차트 상세 정보 조회
    @GetMapping("/detail/{chartNo}")
    @ResponseBody
    public ResponseEntity<ChartDTO> getChartDetail(@PathVariable Long chartNo) {
        ChartDTO chart = chartService.getChartById(chartNo);
        return ResponseEntity.ok(chart);
    }

    // 차트 수정 등록
    @PostMapping("/update/{chartNo}")
    public ResponseEntity<Void> updateChart(@PathVariable Long chartNo, @RequestBody ChartRequestDTO chartRequestDTO) {
        chartService.updateChart(chartNo, chartRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project")
    public String showProjectCharts(Model model) {
        List<ProjectDTO> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "chart/project"; // 이 템플릿 파일로 이동합니다.
    }

}
