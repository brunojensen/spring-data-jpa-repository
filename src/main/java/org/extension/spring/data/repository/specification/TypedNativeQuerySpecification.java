package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

public interface TypedNativeQuerySpecification<E> extends QuerySpecification {

    @Override
    default QueryType type() {
        return QueryType.TYPED_NATIVE;
    }
}
