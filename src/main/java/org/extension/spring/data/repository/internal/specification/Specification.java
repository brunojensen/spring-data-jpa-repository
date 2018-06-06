package org.extension.spring.data.repository.internal.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

public interface Specification {

    default boolean isSatisfied() {
        return true;
    }

    QueryType type();
}
