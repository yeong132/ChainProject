package org.zerock.chain.service;

import org.zerock.chain.dto.ReportDTO;
import org.zerock.chain.dto.ReportRequestDTO;

public interface ReportService {
    ReportDTO createReport(ReportRequestDTO reportRequestDTO);  // 업무보고서 등록
}
