package org.extension.spring.data.repository.internal;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.extension.spring.data.repository.specification.QuerySpecification;

class QuerySpecificationProcessor {

  private QuerySpecificationProcessor() {
  }

  static Query process(EntityManager entityManager, QuerySpecification specification) {
    Objects.requireNonNull(entityManager);
    Objects.requireNonNull(specification);

    final Query query = entityManager.createQuery(specification.query());
    specification.withPredicate(query);
    return query;
  }
}
