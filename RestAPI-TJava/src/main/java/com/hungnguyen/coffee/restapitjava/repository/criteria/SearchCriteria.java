package com.hungnguyen.coffee.restapitjava.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key; // fs, ls, id, email...

    private String operation; // =, <, >

    private Object value;
}
