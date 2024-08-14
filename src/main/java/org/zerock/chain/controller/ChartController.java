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
        chartService.createChart(chartRequestDTO);
        log.info("Chart created with name: {}", chartRequestDTO.getChartName());
        return "redirect:/chart/OKR";
    }


    // 차트 상세 정보 조회 및 수정 모달 표시
    @GetMapping("/detail/{chartNo}")
    @ResponseBody
    public ChartDTO getChartDetail(@PathVariable Long chartNo) {
        // JSON 데이터를 반환하여 클라이언트에서 AJAX로 처리
        return chartService.getChartById(chartNo);
    }

    // 차트 수정 등록
    @PostMapping("/update")
    public String updateChart(ChartRequestDTO chartRequestDTO) {
        chartService.updateChart(chartRequestDTO.getChartNo(), chartRequestDTO);
        return "redirect:/chart/OKR";
    }

    // 차트 삭제
    @PostMapping("/delete")
    public String deleteChart(@RequestParam Long chartNo) {
        chartService.deleteChart(chartNo);
        return "redirect:/chart/OKR";
    }
}
