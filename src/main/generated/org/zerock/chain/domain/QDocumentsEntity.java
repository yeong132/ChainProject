package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDocumentsEntity is a Querydsl query type for DocumentsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDocumentsEntity extends EntityPathBase<DocumentsEntity> {

    private static final long serialVersionUID = -74589358L;

    public static final QDocumentsEntity documentsEntity = new QDocumentsEntity("documentsEntity");

    public final StringPath category = createString("category");

    public final NumberPath<Integer> docNo = createNumber("docNo", Integer.class);

    public final StringPath docStatus = createString("docStatus");

    public final StringPath docTitle = createString("docTitle");

    public final DatePath<java.time.LocalDate> draftDate = createDate("draftDate", java.time.LocalDate.class);

    public final NumberPath<Integer> formNo = createNumber("formNo", Integer.class);

    public final NumberPath<Integer> receiverEmpNo = createNumber("receiverEmpNo", Integer.class);

    public final DatePath<java.time.LocalDate> reqDate = createDate("reqDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> reReqDate = createDate("reReqDate", java.time.LocalDate.class);

    public final NumberPath<Integer> senderEmpNo = createNumber("senderEmpNo", Integer.class);

    public QDocumentsEntity(String variable) {
        super(DocumentsEntity.class, forVariable(variable));
    }

    public QDocumentsEntity(Path<? extends DocumentsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDocumentsEntity(PathMetadata metadata) {
        super(DocumentsEntity.class, metadata);
    }

}

