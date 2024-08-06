package org.zerock.chain.service;

import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.dto.ProjectRequestDTO;

import java.util.List;

public interface ProjectService {
    Long register(ProjectDTO projectDTO);   //  - - 등록
    List<ProjectDTO> getAllProjects();   // -- 목록  list()
    void setProjectFavorite(Long projectNo, boolean projectFavorite); // 즐겨찾기 갱신
    void updateProjectProgress(Long projectNo, Integer projectProgress);  // 그래프 업로드
    ProjectDTO getProjectById(Long projectNo);  // 특정 프로젝트 조회
    void updateProject(Long projectNo, ProjectRequestDTO projectRequestDTO);  // 프로젝트 수정



}
