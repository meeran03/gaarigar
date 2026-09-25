package com.gianteyes.gaarigar.utils;

import lombok.*;

@Getter
@Setter
public class SearchCriteria {
    private String key;
    private Object value;
    private SearchOperation operation;

    public SearchCriteria() {
    }
    public SearchCriteria(String key, Object value) {
        this.key = key;
        this.value = value;
        this.operation = SearchOperation.EQUAL;
    }

    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

}
