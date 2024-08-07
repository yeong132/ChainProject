package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.ReportDTO;
import org.zerock.chain.dto.ReportRequestDTO;
import org.zerock.chain.service.ReportService;

@Controller
@RequestMapping("/report")
@Log4j2
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/list")
    public String workList(Model model) {
        return "report/list";
    }

    // 문서 개별 상세  확인 페이지
    @GetMapping("/detail")
    public String workDetail(Model model) {
        return "report/detail";
    }

    // 문서 수정 페이지
    @GetMapping("/modify")
    public String workModify(Model model) {
        return "report/modify";
    }


    // 새 문서 생성 페이지
    @GetMapping("/register")
    public String workRegister(Model model) {
        return "report/register";
    }

    @PostMapping("/register")
    public @ResponseBody ReportDTO registerPOST(@RequestBody ReportRequestDTO reportRequestDTO) {
        return reportService.createReport(reportRequestDTO);
    }


}
