package com.hungnguyen.coffee.restapitjava.repository.specification;

import com.hungnguyen.coffee.restapitjava.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.hungnguyen.coffee.restapitjava.repository.specification.SearchOperation.ZERO_OR_MORE_REGEX;

public class UserSpecificationBuilder
{
    public  final List<SpecSearchCreteria> param;

    public UserSpecificationBuilder() {
        this.param = param;
    }

    public UserSpecificationBuilder with(String key, String operation, Object value, String prefix, String suffix){
        return with(null ,operation,  value,  prefix,  suffix);
    }

    public UserSpecificationBuilder with(String orPredicate,String key, String operation, Object value, String prefix, String suffix){

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

        param.add(new SpecSearchCreteria(key, searchOperation, value));
        return this;
    }

    public Specification<User> build(){
        if(param.isEmpty()){
            return null;
        }
        Specification<User> specification = new UserSpecification(param.get(0));

        for(int i = 1; i < param.size(); i++){
            specification = param.get(i).getOrPredicate() ?
                    Specification.where(specification).or(new UserSpecification(param.get(i))) :
                    Specification.where(specification).and(new UserSpecification(param.get(i)));
        }

        return specification;
    }
}
