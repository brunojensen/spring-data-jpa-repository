package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Specification to JPQL queries
 */
public interface TypedQuerySpecification extends QuerySpecification {

  /**
   * Don't override me.
   *
   * @return in case of {@link TypedQuerySpecification} must return TYPED
   */
  @Override
  default QueryType type() {
    return QueryType.TYPED;
  }
}
