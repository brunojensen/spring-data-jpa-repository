package org.extension.spring.data.repository.specification;

import javax.persistence.Query;

/**
 * Root interface for the specification pattern. Usually you don't need to deal with this interface,
 * just in case you want to hack the framework.
 */
public interface Specification {

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
