package org.extension.spring.data.repository.specification;

import javax.persistence.Query;
import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Query specification for plain sql statement that doesn't requires any type and have the intention
 * to return non-entity results
 *
 * <p>
 * Usually this will be used with a projectionType on {@link org.extension.spring.data.repository.RepositorySpecificationExecutor#find(Specification,
 * Class)} or {@link org.extension.spring.data.repository.RepositorySpecificationExecutor#findAll(Specification,
 * Class)}
 * </p>
 */
public interface QuerySpecification extends Specification {

  /**
   * Query statement to be executed.
   * <p>In case of parameterized query, use it combined with withPredicate</p>
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
   * Don't override me.
   *
   * @return in case of {@link QuerySpecification} must return PLAIN
   */
  @Override
  default QueryType type() {
    return QueryType.PLAIN;
  }

}
