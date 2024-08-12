package org.zerock.chain.service;

import org.springframework.web.multipart.MultipartFile;
import org.zerock.chain.dto.ProjectDTO;
import org.zerock.chain.dto.ProjectRequestDTO;


import java.util.List;

public interface ProjectService {

    Long register(ProjectDTO projectDTO);   // 등록
    List<ProjectDTO> getAllProjects();   // 목록 조회
    void setProjectFavorite(Long projectNo, boolean projectFavorite); // 즐겨찾기 갱신
    void updateProjectProgress(Long projectNo, Integer projectProgress);  // 진행도 업데이트
    ProjectDTO getProjectById(Long projectNo);  // 특정 프로젝트 조회
    void updateProject(Long projectNo, ProjectRequestDTO projectRequestDTO);  // 프로젝트 수정
    void deleteProject(Long projectNo); // 삭제 기능
    String saveFile(MultipartFile file) throws Exception; // 첨부파일 저장
    List<ProjectDTO> getTemporaryProjects(); // 임시 보관 프로젝트 조회

}
