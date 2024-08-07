package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.zerock.chain.entity.Employee;

// Employee 엔티티에 대한 리포지토리 인터페이스
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // CrudRepository:
    //기본적인 CRUD(Create, Read, Update, Delete) 작업을 위한 메서드를 제공합니다.
    //더 간단하고 가벼운 인터페이스로, 기본적인 CRUD 기능만 필요할 때 사용합니다.

    //JpaRepository:
    //CrudRepository를 확장하며, 추가로 JPA와 관련된 기능을 더 많이 제공합니다.
    //페이징 및 정렬 기능(findAll(Pageable pageable), findAll(Sort sort))을 기본으로 제공하여 더 복잡한 쿼리를 작성하는 데 유리합니다.
    //JPA 특화 기능을 포함하여 더 강력하고 유연한 데이터 접근을 지원합니다.

//    데이터 처리 과정
//인터페이스 정의: CrudRepository를 확장하여 EmployeeRepository 인터페이스를 정의합니다.
//제네릭 타입 지정: CrudRepository<Employee, Integer>를 사용하여, Employee 엔티티와 해당 엔티티의 기본 키 타입(Integer)을 지정합니다.
//기본 CRUD 메서드 사용: CrudRepository에서 제공하는 기본 CRUD 메서드를 사용할 수 있습니다.
//요약 및 핵심
//EmployeeRepository 인터페이스는 Employee 엔티티에 대한 데이터베이스 접근을 관리합니다.
//CrudRepository를 확장하여 기본적인 CRUD 메서드를 제공합니다.
//별도의 구현 없이 기본 CRUD 기능을 사용할 수 있습니다.
}