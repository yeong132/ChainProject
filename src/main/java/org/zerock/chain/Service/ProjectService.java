package org.zerock.chain.Service;

import org.zerock.chain.DTO.ProjectDTO;

import java.util.List;

public interface ProjectService {

    Long register(ProjectDTO projectDTO);  //  - - 등록
    List<ProjectDTO> getAllProjects();  // -- 목록  list()
}
