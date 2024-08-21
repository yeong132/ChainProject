package org.zerock.chain.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.chain.dto.ProjectRequestDTO;
import org.zerock.chain.dto.ReportDTO;
import org.zerock.chain.dto.ReportRequestDTO;
import org.zerock.chain.service.FileService;
import org.zerock.chain.service.ReportService;

import java.util.List;

@Controller
@RequestMapping("/report")
@Log4j2
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileService fileService;

    // 문서 전체 목록 조회
    @GetMapping("/list")
    public String listGET(Model model) {
        List<ReportDTO> reportList = reportService.getAllReports();
        model.addAttribute("reportList", reportList);
        return "report/list";
    }

    // 문서 전체 목록 조회
    @GetMapping("/temporary")
    public String temporaryGET(Model model) {
        List<ReportDTO> temporaryReports = reportService.getTemporaryReports(); // 임시 보관 문서 조회 추가
        model.addAttribute("temporaryReports", temporaryReports); // 임시 보관 문서 모델에 추가
        return "report/temporary";
    }

    // 문서 개별 상세 조회
    @GetMapping("/detail/{reportNo}")
    public String detailGET(@PathVariable Long reportNo, ProjectRequestDTO projectRequestDTO, Model model) {
        ReportDTO reportDTO = reportService.getReportById(reportNo);
        model.addAttribute("report", reportDTO);
        return "report/detail";
    }

    // 문서 수정 페이지
    @GetMapping("/modify/{reportNo}")
    public String modifyGET(@PathVariable("reportNo") long reportNo, Model model) {
        ReportDTO report = reportService.getReportById(reportNo);
        model.addAttribute("report", report);
        return "report/modify";
    }

    // 문서 수정 등록 기능
    @PostMapping("/modify/{reportNo}")
    public String modifyPOST(@PathVariable Long reportNo, @Valid ReportRequestDTO reportRequestDTO, BindingResult bindingResult, @RequestParam("isTemporary") boolean isTemporary, @RequestParam("reportFiles") MultipartFile reportFiles, @RequestParam("existingReportFiles") String existingReportFiles) throws Exception {
        reportRequestDTO.setIsTemporary(isTemporary);

        if (!reportFiles.isEmpty()) {
            // 새로운 파일이 업로드된 경우
            String fileName = fileService.saveFile(reportFiles);
            reportRequestDTO.setReportFiles(fileName);
        } else {
            // 새로운 파일이 업로드되지 않은 경우 기존 파일 정보 유지
            reportRequestDTO.setReportFiles(existingReportFiles);
        }

        reportService.updateReport(reportNo, reportRequestDTO);
        return "redirect:/report/list";
    }

    // 새 문서 생성 조회
    @GetMapping("/register")
    public String registerGET(Model model) {
        model.addAttribute("reportDTO", new ReportDTO());
        return "report/register";
    }

    // 새 문서 등록 처리
    @PostMapping("/register")
    public String registerPOST(@Valid @ModelAttribute ReportDTO reportDTO, BindingResult bindingResult, Model model, @RequestParam("isTemporary") boolean isTemporary) {
        reportDTO.setIsTemporary(isTemporary); // 임시 보관 여부 설정
        reportService.createReport(reportDTO);
        return "redirect:/report/list";
    }

    // 문서 삭제 처리
    @DeleteMapping("/delete/{reportNo}")
    @ResponseBody
    public String deleteReport(@PathVariable Long reportNo) {
        reportService.deleteReport(reportNo);
        return "redirect:/report/list";
    }
}
