package com.hungnguyen.coffee.restapitjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "tbl_group")
public class Group extends AbstractEntity<Integer> {

    private String name;
    private String description;

    @OneToOne
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<Group>  groups = new HashSet<>();

}
