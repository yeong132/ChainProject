package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFavoriteQna is a Querydsl query type for FavoriteQna
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoriteQna extends EntityPathBase<FavoriteQna> {

    private static final long serialVersionUID = 749877954L;

    public static final QFavoriteQna favoriteQna = new QFavoriteQna("favoriteQna");

    public final StringPath faqContent = createString("faqContent");

    public final DateTimePath<java.time.LocalDateTime> faqCreatedDate = createDateTime("faqCreatedDate", java.time.LocalDateTime.class);

    public final StringPath faqName = createString("faqName");

    public final NumberPath<Long> faqNo = createNumber("faqNo", Long.class);

    public QFavoriteQna(String variable) {
        super(FavoriteQna.class, forVariable(variable));
    }

    public QFavoriteQna(Path<? extends FavoriteQna> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFavoriteQna(PathMetadata metadata) {
        super(FavoriteQna.class, metadata);
    }

}

