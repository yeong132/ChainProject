package org.zerock.chain.imjongha.service;

import org.zerock.chain.imjongha.dto.MonthlyAttendanceSummaryDTO;

import java.util.List;

public interface MonthlyAttendanceSummaryService {
    List<MonthlyAttendanceSummaryDTO> getAllSummariesByEmployee(Long empNo);
    List<MonthlyAttendanceSummaryDTO> getSummariesByYearAndMonth(int year, int month);
    MonthlyAttendanceSummaryDTO getSummaryById(Long id);
}
