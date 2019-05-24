package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Root interface for the specification pattern. Usually you don't need to deal with this interface,
 * just in case you want to hack the framework.
 */
public interface Specification {

  /**
   * This method can be override in case you want to setup a requirement for the specification
   * execution
   *
   * @return false if you don't want to execute the specification true (by default).
   */
  default boolean isSatisfied() {
    return true;
  }

  /**
   * Don't override me.
   */
  QueryType type();
}
