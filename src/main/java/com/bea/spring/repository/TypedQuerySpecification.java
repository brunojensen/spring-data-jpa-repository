package com.bea.spring.repository;

import com.bea.spring.repository.internal.annotations.QueryType;

import javax.persistence.Query;

@QueryType(QueryType.Type.TYPED)
public interface TypedQuerySpecification<E> extends PlainQuerySpecification {

    public String query();

    public void toPredicate(Query query);

}
