package org.extension.spring.data.repository.specification;

import javax.persistence.Query;

/**
 * Root sealed interface for the specification pattern. You don't need to deal with this interface.
 *
* @see TypedQuerySpecification
* @see QuerySpecification
* @see NativeQuerySpecification
* @see TypedNativeQuerySpecification
 */
public sealed interface Specification
  permits TypedQuerySpecification, QuerySpecification, NativeQuerySpecification, TypedNativeQuerySpecification {

  /**
  * Query statement to be executed.
  * <p>In case of parameterized query, implement withPredicate method</p>
  */
  String query();

  /**
  * This method should be implemented when you need to setup the parameters before you query is
  * executed.
  *
  * @param query - JPA query to setup the parameters
  */
  default void withPredicate(Query query) {
  }

  /**
   * This method can be override in case you want to setup a requirement for the specification
   * execution
   *
   * @return false if you don't want to execute the specification
   *         true (default).
   */
  default boolean isSatisfied() {
    return true;
  }

}
