package com.gianteyes.gaarigar.utils;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BaseSpecification<T> implements Specification<T> {
    private List<SearchCriteria> list;

    public BaseSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        for (SearchCriteria criteria : list) {
            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                if (criteria.getValue() instanceof LocalDateTime) {
                    predicates.add(builder.greaterThan(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
                }
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                if (criteria.getValue() instanceof LocalDateTime) {
                    predicates.add(builder.lessThan(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
                }
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                if (criteria.getValue() instanceof LocalDateTime) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
                }
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                if (criteria.getValue() instanceof LocalDateTime) {
                    predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
                }
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(builder.notEqual(
                        root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(builder.equal(
                        root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                predicates.add(builder.like(
                        builder.lower(root.get(criteria.getKey())),
                        "%" + criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                predicates.add(builder.like(
                        builder.lower(root.get(criteria.getKey())),
                        criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                predicates.add(builder.like(
                        builder.lower(root.get(criteria.getKey())),
                        "%" + criteria.getValue().toString().toLowerCase()));
            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                predicates.add(builder.in(root.get(criteria.getKey())).value(criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                predicates.add(builder.not(root.get(criteria.getKey())).in(criteria.getValue()));
            }
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

}
