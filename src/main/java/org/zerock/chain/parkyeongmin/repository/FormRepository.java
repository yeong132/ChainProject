package org.zerock.chain.parkyeongmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.parkyeongmin.model.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, String> {
}
