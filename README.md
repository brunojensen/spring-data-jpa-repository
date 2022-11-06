# spring-data-jpa-repository

Specification repository API for complex queries by making use of Repository and Specification patterns. 

[![Build Status](https://travis-ci.org/brunojensen/spring-data-jpa-repository.svg?branch=master)](https://travis-ci.org/brunojensen/spring-data-jpa-repository)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring.data.repository%3Aspring-data-jpa-repository&metric=alert_status)](https://sonarcloud.io/dashboard?id=spring.data.repository%3Aspring-data-jpa-repository)

Demo:
https://github.com/brunojensen/spring-data-repository-demo

Setup:

```java

  @EnableJpaRepositories(
      value = "jpa.model.package",
      repositoryBaseClass = RepositorySpecificationExecutorImpl.class)
  public class ... {
  
  }

```

Usage:

```java

public interface PersonRepository extends RepositorySpecificationExecutor<Person, String> {
  // you still can use spring-data common Repository implementations
  // this framework is only giving you a boost
}

```

```java
...
  public Page<Person> searchBy(final Person person, final Pageable pageable) {
      return repository.findAll(new ByPersonUsingTypedQuerySpecification(person), pageable);
  }

  public List<Person> searchBy(final Person person) {
      return repository.findAll(new ByPersonUsingTypedQuerySpecification(person));
  }

  /**
   * Uses {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository}
   * implementation
   */
  public Person findById(String id) {
      return repository.findById(id).orElse(null);
  }

  /**
   * Uses {@link org.extension.spring.data.repository.specification.TypedNativeQuerySpecification}
   * combined with {@link org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping}
   * to use SqlResultMapping to serialize the query result into the pre-defined object.
   */
  public List<PersonContactResultMapping> findAll2() {
      return repository.findAll(new ByPersonContactUsingTypedNativeSpecification(), PersonContactResultMapping.class);
  }

  /**
   * Still capable to quering using annotation {@link org.springframework.data.jpa.repository.Query} and
   * interface projection
   */
  public Page<PersonRecord> findAll(final Pageable pageable) {
      return repository.findPersonRecord(pageable);
  }

  /**
   * Uses {@link org.extension.spring.data.repository.specification.TypedNativeQuerySpecification}
   *      * for count
   */
  public List<Person> findAll() {
      return repository.findAll((TypedQuerySpecification<Person>) () -> "SELECT * FROM Person");
  }

  /**
   * Uses {@link org.extension.spring.data.repository.specification.TypedNativeQuerySpecification} with
   * SELECT * ... for counting.
   *
   * The query will be replaced with SELECT count(*)... at execution time.
   */
  public long count() {
      return repository.count(new ByPersonUsingTypedNativeSpecification());
  }

  /**
   * Uses {@link org.extension.spring.data.repository.specification.NativeQuerySpecification} with
   * SELECT * ... for counting.
   *
   * The query will be replaced with SELECT count(*)... at execution time.
   */
  public long countBy(final Person person) {
      return repository.count(new ByPersonUsingTypedQuerySpecification(person);
  }
...

```
