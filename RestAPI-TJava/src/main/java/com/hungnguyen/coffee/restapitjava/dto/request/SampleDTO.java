package com.hungnguyen.coffee.restapitjava.dto.request;

import org.apache.commons.lang3.builder.HashCodeExclude;

import java.io.Serializable ;

//@Data = @Getter + @Setter + @EqualsAndHashCode + @ToString

public class SampleDTO implements Serializable {
    private Integer id;
    private String name;
}
