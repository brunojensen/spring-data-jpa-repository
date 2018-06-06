package com.bea.spring.repository;

import com.bea.spring.repository.internal.annotations.QueryType;
import org.springframework.data.jpa.domain.Specification;

@QueryType(QueryType.Type.CRITERIA)
public interface CriteriaQuerySpecification<T> extends Specification<T>, DefaultSpecification<T> {

}
