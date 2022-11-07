package org.extension.spring.data.repository.specification;

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
public non-sealed interface QuerySpecification extends Specification {

}
