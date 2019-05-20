package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Specification for JPA {@link javax.persistence.criteria.CriteriaQuery}
 */
public interface CriteriaQuerySpecification<T> extends org.springframework.data.jpa.domain.Specification<T>, Specification {

    /**
     * Don't override me.
     *
     * @return in case of {@link CriteriaQuerySpecification} must return CRITERIA
     */
    @Override
    default QueryType type() {
        return QueryType.CRITERIA;
    }

}
