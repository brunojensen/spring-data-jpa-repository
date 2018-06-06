package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;
import org.extension.spring.data.repository.internal.specification.Specification;

public interface CriteriaQuerySpecification<T> extends org.springframework.data.jpa.domain.Specification<T>, Specification {

    @Override
    default QueryType type() {
        return QueryType.CRITERIA;
    }

}
