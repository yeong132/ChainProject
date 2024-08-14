package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQna is a Querydsl query type for Qna
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQna extends EntityPathBase<Qna> {

    private static final long serialVersionUID = 2146495486L;

    public static final QQna qna = new QQna("qna");

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath qnaAuthor = createString("qnaAuthor");

    public final StringPath qnaCategory = createString("qnaCategory");

    public final StringPath qnaContent = createString("qnaContent");

    public final StringPath qnaFiles = createString("qnaFiles");

    public final StringPath qnaName = createString("qnaName");

    public final NumberPath<Long> qnaNo = createNumber("qnaNo", Long.class);

    public final BooleanPath qnaStatus = createBoolean("qnaStatus");

    public final DateTimePath<java.time.LocalDateTime> qnaUploadDate = createDateTime("qnaUploadDate", java.time.LocalDateTime.class);

    public QQna(String variable) {
        super(Qna.class, forVariable(variable));
    }

    public QQna(Path<? extends Qna> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQna(PathMetadata metadata) {
        super(Qna.class, metadata);
    }

}

