package com.hungnguyen.coffee.restapitjava.repository.specification;

import com.hungnguyen.coffee.restapitjava.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec{

    public static Specification<User> hasFirstName(String firstName){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like((root.get("firstName").as(String.class)), "%"+firstName+"%");
    }

    public static Specification<User> notEqualGender(String gender){
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual((root.get("gender").as(String.class)), gender);
    }
}
