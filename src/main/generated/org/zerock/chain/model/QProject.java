package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = -664607693L;

    public static final QProject project = new QProject("project");

    public final StringPath dmpNo = createString("dmpNo");

    public final StringPath participants = createString("participants");

    public final DatePath<java.time.LocalDate> projectEndDate = createDate("projectEndDate", java.time.LocalDate.class);

    public final BooleanPath projectFavorite = createBoolean("projectFavorite");

    public final StringPath projectName = createString("projectName");

    public final NumberPath<Long> projectNo = createNumber("projectNo", Long.class);

    public final NumberPath<Integer> projectProgress = createNumber("projectProgress", Integer.class);

    public final DatePath<java.time.LocalDate> projectStartDate = createDate("projectStartDate", java.time.LocalDate.class);

    public QProject(String variable) {
        super(Project.class, forVariable(variable));
    }

    public QProject(Path<? extends Project> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProject(PathMetadata metadata) {
        super(Project.class, metadata);
    }

}

