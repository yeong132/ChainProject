package org.zerock.chain.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFormDataNo is a Querydsl query type for FormDataNo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFormDataNo extends BeanPath<FormDataNo> {

    private static final long serialVersionUID = 1086777336L;

    public static final QFormDataNo formDataNo = new QFormDataNo("formDataNo");

    public final NumberPath<Integer> docNo = createNumber("docNo", Integer.class);

    public final NumberPath<Integer> fieldNo = createNumber("fieldNo", Integer.class);

    public QFormDataNo(String variable) {
        super(FormDataNo.class, forVariable(variable));
    }

    public QFormDataNo(Path<? extends FormDataNo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFormDataNo(PathMetadata metadata) {
        super(FormDataNo.class, metadata);
    }

}

