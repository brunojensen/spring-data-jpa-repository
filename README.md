# spring-data-repository

Specification repository API for complex quering... read again COMPLEX QUERING...

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

public interface PersonRepository extends RepositorySpecificationExecutor<Person, String> { }

```

```java
...

public List<Person> searchBy(final Person person) {
    return repository.searchBy(new TypedQuerySpecification<Person>() {
        @Override
        public String query() {
            return "FROM Person";
        }

        @Override
        public void toPredicate(Query query) {
        }
    });
}

public long countBy(final Person person) {
    return repository.count(new TypedQuerySpecification<Person>() {
        @Override
        public String query() {
            return "SELECT count(*) FROM Person";
        }

        @Override
        public void toPredicate(Query query) {
        }
    });
}

public Person findById(String id) {
    return repository.findBy(new CriteriaQuerySpecification<Person>() {
        @Override
        public Predicate toPredicate(Root<Person> r, CriteriaQuery<?> cq, CriteriaBuilder cb) {
            return cb.equal(r.get("email"), id);
        }
    });
}

...

```
