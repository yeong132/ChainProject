package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmpRank is a Querydsl query type for EmpRank
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmpRank extends EntityPathBase<EmpRank> {

    private static final long serialVersionUID = -1980154322L;

    public static final QEmpRank empRank = new QEmpRank("empRank");

    public final StringPath rankName = createString("rankName");

    public final NumberPath<Long> rankNo = createNumber("rankNo", Long.class);

    public QEmpRank(String variable) {
        super(EmpRank.class, forVariable(variable));
    }

    public QEmpRank(Path<? extends EmpRank> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmpRank(PathMetadata metadata) {
        super(EmpRank.class, metadata);
    }

}

