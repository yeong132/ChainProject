package org.zerock.chain.pse.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zerock.chain.pse.dto.ChartDTO;
import org.zerock.chain.pse.dto.ChartRequestDTO;
import org.zerock.chain.pse.model.Chart;
import org.zerock.chain.pse.repository.ChartRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ChartServiceImpl extends BaseService<Chart> implements ChartService {

    private final ChartRepository chartRepository;
    private final ModelMapper modelMapper;

    @Override
    protected List<Chart> getAllItemsByEmpNo(Long empNo) {
        // Retrieve charts by employee number (chartAuthor)
        return chartRepository.findByChartAuthor(empNo);
    }

    @Override   // 차트 생성 등록
    public ChartDTO createChart(ChartRequestDTO chartRequestDTO) {
        // Get employee number from session
        Long empNo = getEmpNoFromSession();
        chartRequestDTO.setChartAuthor(empNo);

        Chart chart = modelMapper.map(chartRequestDTO, Chart.class);

        // Process progress labels
        String labelsString = String.join(",",
                chartRequestDTO.getProgressLabel20(),
                chartRequestDTO.getProgressLabel40(),
                chartRequestDTO.getProgressLabel60(),
                chartRequestDTO.getProgressLabel80(),
                chartRequestDTO.getProgressLabel100()
        );
        chart.setProgressLabels(labelsString);

        chart = chartRepository.save(chart);
        return modelMapper.map(chart, ChartDTO.class);
    }

    @Override   // 전체 차트 조회
    public List<ChartDTO> getAllCharts() {
        Long empNo = getEmpNoFromSession();
        List<Chart> charts = getItemsByEmpNo(empNo, chartRepository::findByChartAuthor);
        return charts.stream()
                .map(chart -> modelMapper.map(chart, ChartDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 특정 차트 조회
    public ChartDTO getChartById(Long chartNo) {
        Long empNo = getEmpNoFromSession();
        Chart chart = chartRepository.findByChartNoAndChartAuthor(chartNo, empNo)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chart ID or unauthorized access"));
        return modelMapper.map(chart, ChartDTO.class);
    }

    @Override   // 수정 등록
    public ChartDTO updateChart(Long chartNo, ChartRequestDTO chartRequestDTO) {
        Long empNo = getEmpNoFromSession();
        Chart chart = chartRepository.findByChartNoAndChartAuthor(chartNo, empNo)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chart ID or unauthorized access"));

        modelMapper.map(chartRequestDTO, chart);

        // Process progress labels
        String labelsString = String.join(",",
                chartRequestDTO.getProgressLabel20(),
                chartRequestDTO.getProgressLabel40(),
                chartRequestDTO.getProgressLabel60(),
                chartRequestDTO.getProgressLabel80(),
                chartRequestDTO.getProgressLabel100()
        );

        chart.setProgressLabels(labelsString);
        chart = chartRepository.save(chart);
        return modelMapper.map(chart, ChartDTO.class);
    }

    @Override   // 차트 삭제
    public void deleteChart(Long chartNo) {
        Long empNo = getEmpNoFromSession();
        Chart chart = chartRepository.findByChartNoAndChartAuthor(chartNo, empNo)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chart ID or unauthorized access"));
        chartRepository.delete(chart);
    }

    // Method to get employee number from session
    private Long getEmpNoFromSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        return (Long) session.getAttribute("empNo");
    }
}
