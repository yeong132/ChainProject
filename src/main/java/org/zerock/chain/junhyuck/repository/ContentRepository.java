package org.zerock.chain.junhyuck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.junhyuck.model.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}