package org.zerock.chain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.ReportDTO;
import org.zerock.chain.dto.ReportRequestDTO;
import org.zerock.chain.model.Report;
import org.zerock.chain.repository.ReportRepository;


@Service
@Log4j2
@RequiredArgsConstructor
public class ReportServiceImpl implements  ReportService{

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReportDTO createReport(ReportRequestDTO reportRequestDTO) {
        // RequestDTO를 Entity로 변환
        Report report = modelMapper.map(reportRequestDTO, Report.class);

        // 데이터베이스에 저장
        Report savedReport = reportRepository.save(report);

        // 저장된 Entity를 다시 DTO로 변환
        return modelMapper.map(savedReport, ReportDTO.class);
    }
}
