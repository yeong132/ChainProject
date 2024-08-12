package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotice is a Querydsl query type for Notice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotice extends EntityPathBase<Notice> {

    private static final long serialVersionUID = -1605340546L;

    public static final QNotice notice = new QNotice("notice");

    public final StringPath noticeAuthor = createString("noticeAuthor");

    public final StringPath noticeContent = createString("noticeContent");

    public final DateTimePath<java.time.LocalDateTime> noticeCreatedDate = createDateTime("noticeCreatedDate", java.time.LocalDateTime.class);

    public final StringPath noticeFiles = createString("noticeFiles");

    public final StringPath noticeName = createString("noticeName");

    public final NumberPath<Long> noticeNo = createNumber("noticeNo", Long.class);

    public final BooleanPath noticePinned = createBoolean("noticePinned");

    public final DatePath<java.time.LocalDate> noticePinnedDate = createDate("noticePinnedDate", java.time.LocalDate.class);

    public QNotice(String variable) {
        super(Notice.class, forVariable(variable));
    }

    public QNotice(Path<? extends Notice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotice(PathMetadata metadata) {
        super(Notice.class, metadata);
    }

}

