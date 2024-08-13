package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFormDataEntity is a Querydsl query type for FormDataEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFormDataEntity extends EntityPathBase<FormDataEntity> {

    private static final long serialVersionUID = 594463386L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFormDataEntity formDataEntity = new QFormDataEntity("formDataEntity");

    public final StringPath fieldValue = createString("fieldValue");

    public final QFormDataNo formDataNo;

    public QFormDataEntity(String variable) {
        this(FormDataEntity.class, forVariable(variable), INITS);
    }

    public QFormDataEntity(Path<? extends FormDataEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFormDataEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFormDataEntity(PathMetadata metadata, PathInits inits) {
        this(FormDataEntity.class, metadata, inits);
    }

    public QFormDataEntity(Class<? extends FormDataEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.formDataNo = inits.isInitialized("formDataNo") ? new QFormDataNo(forProperty("formDataNo")) : null;
    }

}

