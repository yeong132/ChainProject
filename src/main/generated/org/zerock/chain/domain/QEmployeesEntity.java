package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmployeesEntity is a Querydsl query type for EmployeesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployeesEntity extends EntityPathBase<EmployeesEntity> {

    private static final long serialVersionUID = 1543694943L;

    public static final QEmployeesEntity employeesEntity = new QEmployeesEntity("employeesEntity");

    public final StringPath email = createString("email");

    public final NumberPath<Long> empNo = createNumber("empNo", Long.class);

    public final StringPath firstName = createString("firstName");

    public final DatePath<java.time.LocalDate> hireDate = createDate("hireDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> lastDate = createDate("lastDate", java.time.LocalDate.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath profileImg = createString("profileImg");

    public QEmployeesEntity(String variable) {
        super(EmployeesEntity.class, forVariable(variable));
    }

    public QEmployeesEntity(Path<? extends EmployeesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmployeesEntity(PathMetadata metadata) {
        super(EmployeesEntity.class, metadata);
    }

}

