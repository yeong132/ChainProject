package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTodo is a Querydsl query type for Todo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodo extends EntityPathBase<Todo> {

    private static final long serialVersionUID = 2116941164L;

    public static final QTodo todo = new QTodo("todo");

    public final StringPath todoCategory = createString("todoCategory");

    public final StringPath todoContent = createString("todoContent");

    public final DateTimePath<java.time.LocalDateTime> todoCreatedDate = createDateTime("todoCreatedDate", java.time.LocalDateTime.class);

    public final BooleanPath todoFavorite = createBoolean("todoFavorite");

    public final NumberPath<Long> todoNo = createNumber("todoNo", Long.class);

    public final BooleanPath todoStatus = createBoolean("todoStatus");

    public QTodo(String variable) {
        super(Todo.class, forVariable(variable));
    }

    public QTodo(Path<? extends Todo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTodo(PathMetadata metadata) {
        super(Todo.class, metadata);
    }

}

