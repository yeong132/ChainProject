package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFormFieldsEntity is a Querydsl query type for FormFieldsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFormFieldsEntity extends EntityPathBase<FormFieldsEntity> {

    private static final long serialVersionUID = 350792489L;

    public static final QFormFieldsEntity formFieldsEntity = new QFormFieldsEntity("formFieldsEntity");

    public final StringPath category = createString("category");

    public final StringPath fieldName = createString("fieldName");

    public final NumberPath<Integer> fieldNo = createNumber("fieldNo", Integer.class);

    public final StringPath fieldOptions = createString("fieldOptions");

    public final StringPath fieldType = createString("fieldType");

    public final NumberPath<Integer> formNo = createNumber("formNo", Integer.class);

    public QFormFieldsEntity(String variable) {
        super(FormFieldsEntity.class, forVariable(variable));
    }

    public QFormFieldsEntity(Path<? extends FormFieldsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFormFieldsEntity(PathMetadata metadata) {
        super(FormFieldsEntity.class, metadata);
    }

}

