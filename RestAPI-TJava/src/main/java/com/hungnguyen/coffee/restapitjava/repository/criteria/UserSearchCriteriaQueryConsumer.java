package com.hungnguyen.coffee.restapitjava.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteriaQueryConsumer implements Consumer<SearchCriteria> {

    private CriteriaBuilder cb;
    private Predicate predicate;
    private Root root;

    @Override
    public void accept(SearchCriteria param) {
        if(param.getOperation().equals(">")){

            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get(param.getKey()),
                    param.getValue().toString()));
        } else if(param.getOperation().equals("<")){
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get(param.getKey()),param.getValue().toString()));
        } else{ // =
            if(root.get(param.getKey()).getJavaType() == String.class){
                predicate = cb.and(predicate, cb.like(root.get(param.getKey()),"%" + param.getValue().toString() +
                        "%"));
            }else{
                predicate = cb.and(predicate, cb.equal(root.get(param.getKey()), param.getValue().toString()));
            }
        }
    }
}
