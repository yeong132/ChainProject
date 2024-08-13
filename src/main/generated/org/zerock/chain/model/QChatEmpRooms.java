package org.zerock.chain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatEmpRooms is a Querydsl query type for ChatEmpRooms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatEmpRooms extends EntityPathBase<ChatEmpRooms> {

    private static final long serialVersionUID = -918321746L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatEmpRooms chatEmpRooms = new QChatEmpRooms("chatEmpRooms");

    public final NumberPath<Long> chatNo = createNumber("chatNo", Long.class);

    public final QChatRoom chatRoom;

    public final QEmployee employee;

    public QChatEmpRooms(String variable) {
        this(ChatEmpRooms.class, forVariable(variable), INITS);
    }

    public QChatEmpRooms(Path<? extends ChatEmpRooms> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatEmpRooms(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatEmpRooms(PathMetadata metadata, PathInits inits) {
        this(ChatEmpRooms.class, metadata, inits);
    }

    public QChatEmpRooms(Class<? extends ChatEmpRooms> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom")) : null;
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

