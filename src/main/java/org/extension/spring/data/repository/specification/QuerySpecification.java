package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;
import org.extension.spring.data.repository.internal.specification.Specification;

import javax.persistence.Query;

public interface QuerySpecification extends Specification {

    String query();

    void toPredicate(Query query);

    @Override
    default QueryType type() {
        return QueryType.PLAIN;
    }

}
