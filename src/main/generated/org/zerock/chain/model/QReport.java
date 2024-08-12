package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = -1500172070L;

    public static final QReport report = new QReport("report");

    public final BooleanPath isTemporary = createBoolean("isTemporary");

    public final StringPath meetingRoom = createString("meetingRoom");

    public final StringPath meetingTime = createString("meetingTime");

    public final StringPath reportAuthor = createString("reportAuthor");

    public final StringPath reportCategory = createString("reportCategory");

    public final StringPath reportContent = createString("reportContent");

    public final StringPath reportFiles = createString("reportFiles");

    public final StringPath reportName = createString("reportName");

    public final NumberPath<Long> reportNo = createNumber("reportNo", Long.class);

    public final StringPath reportParticipants = createString("reportParticipants");

    public final DatePath<java.time.LocalDate> reportUploadDate = createDate("reportUploadDate", java.time.LocalDate.class);

    public QReport(String variable) {
        super(Report.class, forVariable(variable));
    }

    public QReport(Path<? extends Report> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReport(PathMetadata metadata) {
        super(Report.class, metadata);
    }

}

