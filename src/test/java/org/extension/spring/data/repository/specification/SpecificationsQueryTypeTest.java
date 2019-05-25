package org.extension.spring.data.repository.specification;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.extension.spring.data.repository.internal.enumeration.QueryType;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class SpecificationsQueryTypeTest {

  @Test
  public void testNativeQuerySpecificationReturnsNativeQueryType() {
    final NativeQuerySpecification s = () -> "Select 1";
    Assert.assertThat(s.type(), IsEqual.equalTo(QueryType.NATIVE));
  }

  @Test
  public void testQuerySpecificationReturnsPlainQueryType() {
    final QuerySpecification s = () -> "Select 1";
    Assert.assertThat(s.type(), IsEqual.equalTo(QueryType.PLAIN));
  }

  @Test
  public void testTypedNativeQuerySpecificationReturnsTypedNativeQueryType() {
    final TypedNativeQuerySpecification s = () -> "Select 1";
    Assert.assertThat(s.type(), IsEqual.equalTo(QueryType.TYPED_NATIVE));
  }

  @Test
  public void testTypedQuerySpecificationReturnsTypedQueryType() {
    final TypedQuerySpecification s = () -> "Select 1";
    Assert.assertThat(s.type(), IsEqual.equalTo(QueryType.TYPED));
  }

  @Test
  public void testCriteriaQuerySpecificationReturnsCriteriaQueryType() {
    final CriteriaQuerySpecification<Person> s = (root, query, criteriaBuilder) -> query.getRestriction();
    Assert.assertThat(s.type(), IsEqual.equalTo(QueryType.CRITERIA));
  }

  @Entity
  public final class Person {

    @Id
    private String id;

    private String name;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
