package org.zerock.chain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.repository.ProjectRepository;
import org.zerock.chain.model.Project;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Override   // 프로젝트 생성 등록 메소드
    public Long register(ProjectDTO projectDTO) {
        log.info("projectDTO ==" + projectDTO);
        Project project = modelMapper.map(projectDTO, Project.class);
        log.info("Mapped Project entity: " + project);
        Long projectNo = projectRepository.save(project).getProjectNo();
        return projectNo;
    }

    @Override   // 프로젝트 목록 조회 메소드
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

}