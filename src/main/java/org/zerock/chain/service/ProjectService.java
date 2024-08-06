package org.zerock.chain.service;

import org.zerock.chain.dto.ProjectDTO;

import java.util.List;

public interface ProjectService {

    Long register(ProjectDTO projectDTO);  //  - - 등록
    List<ProjectDTO> getAllProjects();  // -- 목록  list()
}
