package com.bea.spring.repository;

import com.bea.spring.repository.internal.annotations.QueryType;

import javax.persistence.Query;

@QueryType(QueryType.Type.PLAIN)
public interface PlainQuerySpecification extends DefaultSpecification<Void> {

    public String query();

    public void toPredicate(Query query);

}
