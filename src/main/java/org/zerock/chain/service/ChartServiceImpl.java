package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.ChartDTO;
import org.zerock.chain.dto.ChartRequestDTO;
import org.zerock.chain.model.Chart;
import org.zerock.chain.repository.ChartRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

    private final ModelMapper modelMapper;
    private final ChartRepository chartRepository;
    private final ProjectService projectService;

    public ChartServiceImpl(ModelMapper modelMapper, ChartRepository chartRepository, ProjectService projectService) {
        this.modelMapper = modelMapper;
        this.chartRepository = chartRepository;
        this.projectService = projectService;
    }

    @Override   // 차트 생성 등록
    public ChartDTO createChart(ChartRequestDTO chartRequestDTO) {
        Chart chart = modelMapper.map(chartRequestDTO, Chart.class);

        // 다른 라벨 값 처리 로직
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
        return chartRepository.findAll().stream()
                .map(chart -> modelMapper.map(chart, ChartDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 특정 차트 조회
    public ChartDTO getChartById(Long chartNo) {
        Chart chart = chartRepository.findById(chartNo).orElseThrow();
        return modelMapper.map(chart, ChartDTO.class);
    }


    @Override   // 수정 등록
    public ChartDTO updateChart(Long chartNo, ChartRequestDTO chartRequestDTO) {
        Chart chart = chartRepository.findById(chartNo).orElseThrow();

        modelMapper.map(chartRequestDTO, chart);

        // 각 라벨 값을 쉼표로 구분하여 하나의 문자열로 결합
        String labelsString = String.join(",",
                chartRequestDTO.getProgressLabel20(),
                chartRequestDTO.getProgressLabel40(),
                chartRequestDTO.getProgressLabel60(),
                chartRequestDTO.getProgressLabel80(),
                chartRequestDTO.getProgressLabel100()
        );

        // 결합된 라벨 문자열을 엔티티에 저장
        chart.setProgressLabels(labelsString);

        chart = chartRepository.save(chart);
        return modelMapper.map(chart, ChartDTO.class);
    }

    @Override   // 차트 삭제
    public void deleteChart(Long chartNo) {
        chartRepository.deleteById(chartNo);
    }


}
