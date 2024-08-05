package org.zerock.chain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
