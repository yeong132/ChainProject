package org.zerock.chain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.dto.ProjectRequestDTO;
import org.zerock.chain.repository.ProjectRepository;
import org.zerock.chain.model.Project;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Override   // 프로젝트 생성 등록 메서드
    public Long register(ProjectDTO projectDTO) {
        log.info("projectDTO ==" + projectDTO);
        Project project = modelMapper.map(projectDTO, Project.class);
        log.info("Mapped Project entity: " + project);
        Long projectNo = projectRepository.save(project).getProjectNo();
        return projectNo;
    }

    @Override   // 프로젝트 전체 목록 조회 메서드
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 즐겨찾기 업로드
    public void setProjectFavorite(Long projectNo, boolean projectFavorite) {
        Project project = projectRepository.findById(projectNo)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 프로젝트 ID"));
        project.setProjectFavorite(projectFavorite);
        projectRepository.save(project);
    }

    @Override // 진행도 그래프 업로드
    public void updateProjectProgress(Long projectNo, Integer projectProgress) {
        Project project = projectRepository.findById(projectNo)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 프로젝트 ID"));
        project.setProjectProgress(projectProgress);
        projectRepository.save(project);
    }

    @Override // 특정 프로젝트 조회 기능
    public ProjectDTO getProjectById(Long projectNo) {
        Optional<Project> result = projectRepository.findById(projectNo);
        Project project = result.orElseThrow();
        ProjectDTO projectDTO = modelMapper.map(project, ProjectDTO.class);
        return projectDTO;
    }

    @Override  // 특정 프로젝트 수정 기능
    public void updateProject(Long projectNo, ProjectRequestDTO projectRequestDTO) {
        Optional<Project> result = projectRepository.findById(projectNo);
        Project project = result.orElseThrow(() -> new NoSuchElementException("Project not found"));
        modelMapper.map(projectRequestDTO, project);
        projectRepository.save(project);
    }
}
