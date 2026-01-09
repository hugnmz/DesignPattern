package com.hungnguyen.coffee.restapitjava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.ui.Model;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_address")
public class Address extends AbstractEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "floor")
    private String floor;

    @Column(name = "building")
    private String building;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @Column(name = "address_type")
    private Integer addressType;



}
