package com.hungnguyen.coffee.restapitjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.security.Permission;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role_has_permision")
public class RoleHasPermision extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role name;

    @ManyToOne
    @JoinColumn(name = "permision_id")
    private Permission permission;
}
