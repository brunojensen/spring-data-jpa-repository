package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

public interface NativeQuerySpecification<E> extends QuerySpecification {

    @Override
    default QueryType type() {
        return QueryType.NATIVE;
    }
}
