package com.hungnguyen.coffee.restapitjava.repository.specification;

import lombok.Getter;
import lombok.Setter;

import static com.hungnguyen.coffee.restapitjava.repository.specification.SearchOperation.OR_PREDICATE_FLAG;
import static com.hungnguyen.coffee.restapitjava.repository.specification.SearchOperation.ZERO_OR_MORE_REGEX;

@Getter
public class SpecSearchCreteria {
    private String key;
    private SearchOperation operation;
    private  Object value;
    private Boolean orPredicate;

    public SpecSearchCreteria(String key, SearchOperation searchOperation, Object value) {
        super();
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

    public SpecSearchCreteria(String orPredicate,String key, SearchOperation searchOperation, Object value) {
        super();
        this.orPredicate = orPredicate != null &&  orPredicate.equals(OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

    public SpecSearchCreteria(String key, String operation, String value, String prefix, String suffix){
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));

        if(searchOperation == SearchOperation.EQUALITY){
            boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
            boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

            if(startWithAsterisk && endWithAsterisk){
                searchOperation = SearchOperation.CONTAINS;
            }else if(startWithAsterisk){
                searchOperation = SearchOperation.ENDS_WITH;
            }else{
                searchOperation = SearchOperation.STARTS_WITH;
            }
        }

        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }
}
