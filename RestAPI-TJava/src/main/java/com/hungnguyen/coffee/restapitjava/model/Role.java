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
@Table(name = "tbl_role")
public class Role extends AbstractEntity<Integer> {


    private String name;
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermision> permision = new HashSet<>();
    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> roles = new HashSet<>();

}
