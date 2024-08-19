package org.zerock.chain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Department;
import org.zerock.chain.model.Employee;
import org.zerock.chain.model.Rank;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Employee> findEmployeesByCriteria(String name, String departmentName, String rankName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null) {
            predicates.add(cb.or(
                    cb.like(employee.get("firstName"), "%" + name + "%"),
                    cb.like(employee.get("lastName"), "%" + name + "%")
            ));
        }

        if (departmentName != null) {
            Join<Employee, Department> departmentJoin = employee.join("department");
            predicates.add(cb.equal(departmentJoin.get("dmpName"), departmentName));
        }

        if (rankName != null) {
            Join<Employee, Rank> rankJoin = employee.join("rank");
            predicates.add(cb.equal(rankJoin.get("rankName"), rankName));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
