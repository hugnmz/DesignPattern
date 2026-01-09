package com.hungnguyen.coffee.restapitjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_permision")
public class Permision extends AbstractEntity<Integer>{

    private String name;
    private String description;

    @OneToMany(mappedBy = "permision")
    private Set<RoleHasPermision> roles = new HashSet<>();


}
