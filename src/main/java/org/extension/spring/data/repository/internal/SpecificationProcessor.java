package org.extension.spring.data.repository.internal;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public interface SpecificationProcessor<T, Q extends Query> {

  Q process(EntityManager entityManager, T specification, Class<?> domainClass);
}
