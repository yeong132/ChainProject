package org.zerock.chain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.chain.dto.ReportDTO;
import org.zerock.chain.dto.ReportRequestDTO;
import org.zerock.chain.model.Report;
import org.zerock.chain.repository.ReportRepository;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

    @Override   // 생성 등록
    public ReportDTO createReport(ReportDTO reportDTO) {
        Report report = modelMapper.map(reportDTO, Report.class);
        Report savedReport = reportRepository.save(report);
        return modelMapper.map(savedReport, ReportDTO.class);
    }

    @Override    // 전체 목록 조회
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 임시보관
    public List<ReportDTO> getTemporaryReports() {
        List<Report> reports = reportRepository.findByIsTemporary(true);
        return reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 특정 문서 조회 (수정조회)
    public ReportDTO getReportById(Long reportNo) {
        Report report = reportRepository.findById(reportNo).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        return modelMapper.map(report, ReportDTO.class);
    }

    @Override // 수정 등록
    public void updateReport(Long reportNo, ReportRequestDTO reportRequestDTO) {
        Optional<Report> result = reportRepository.findById(reportNo);
        Report report = result.orElseThrow();
        modelMapper.map(reportRequestDTO, report);
        reportRepository.save(report);
    }

    @Override   // 첨부파일 저장
    public String saveFile(MultipartFile file) throws Exception {
        String uploadDir = "uploads/";
        String originalFilename = file.getOriginalFilename();
        String filePath = uploadDir + originalFilename;
        File destinationFile = new File(filePath);

        // 업로드 디렉토리가 존재하지 않으면 생성
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 파일 저장
        file.transferTo(destinationFile);
        return filePath;
    }

    @Override
    public void deleteReport(Long reportNo) {
        reportRepository.deleteById(reportNo);
    }

}
