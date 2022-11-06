package org.extension.spring.data.repository.specification;

/**
 * Specification to typed native queries.
 *
 * <p>Can be used with ResultSetMapping by annotation the DTO with {@link
 * org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping}
 * or with the JPA entity.
 * </p>
 */
public interface TypedNativeQuerySpecification  extends Specification {

}
