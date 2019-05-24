package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Specification to untyped native queries.
 */
public interface NativeQuerySpecification extends QuerySpecification {

  /**
   * Don't override me.
   *
   * @return in case of {@link NativeQuerySpecification} must return NATIVE
   */
  @Override
  default QueryType type() {
    return QueryType.NATIVE;
  }
}
