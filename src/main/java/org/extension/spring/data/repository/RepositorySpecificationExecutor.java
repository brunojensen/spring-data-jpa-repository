package org.extension.spring.data.repository;

import java.io.Serializable;
import java.util.List;
import org.extension.spring.data.repository.specification.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface RepositorySpecificationExecutor<T, I extends Serializable> extends
    Repository<T, I> {

  T find(Specification specification);

  List<T> findAll(Specification specification);

  Page<T> findAll(Specification specification, Pageable pageable);

  <P> P find(Specification specification, Class<P> projectionType);

  <P> List<P> findAll(Specification specification, Class<P> projectionType);

  <P> Page<P> findAll(Specification specification, Pageable pageable, Class<P> projectionType);

  long count(Specification specification);

}