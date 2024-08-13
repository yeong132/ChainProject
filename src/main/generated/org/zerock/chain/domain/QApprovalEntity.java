package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalEntity is a Querydsl query type for ApprovalEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalEntity extends EntityPathBase<ApprovalEntity> {

    private static final long serialVersionUID = 1137862287L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApprovalEntity approvalEntity = new QApprovalEntity("approvalEntity");

    public final DatePath<java.time.LocalDate> approvalDate = createDate("approvalDate", java.time.LocalDate.class);

    public final NumberPath<Integer> approvalNo = createNumber("approvalNo", Integer.class);

    public final QDocumentsEntity docNo;

    public final QEmployeesEntity empNo;

    public final StringPath rejectionReason = createString("rejectionReason");

    public QApprovalEntity(String variable) {
        this(ApprovalEntity.class, forVariable(variable), INITS);
    }

    public QApprovalEntity(Path<? extends ApprovalEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApprovalEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApprovalEntity(PathMetadata metadata, PathInits inits) {
        this(ApprovalEntity.class, metadata, inits);
    }

    public QApprovalEntity(Class<? extends ApprovalEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.docNo = inits.isInitialized("docNo") ? new QDocumentsEntity(forProperty("docNo")) : null;
        this.empNo = inits.isInitialized("empNo") ? new QEmployeesEntity(forProperty("empNo")) : null;
    }

}

