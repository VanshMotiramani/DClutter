package com.declutter.dclutter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    @EqualsAndHashCode.Include
    private Long addressId;

    @NotBlank(message = "Street name must be at least 5 characters")
    @Size(min = 5)
    @Column(name = "street")
    private String street;

    @NotBlank(message = "Building name must be at least 5 characters")
    @Size(min = 5)
    @Column(name = "building_name")
    private String buildingName;

    @NotBlank(message = "City name must be at least 3 characters")
    @Size(min = 3)
    @Column(name = "city")
    private String city;

    @NotBlank(message = "State name must be at least 2 characters")
    @Size(min = 2)
    @Column(name = "state")
    private String state;

    @NotBlank(message = "Country name must be at least 2 characters")
    @Size(min = 2)
    @Column(name = "country")
    private String country;

    @NotBlank(message = "Pincode must be exactly 6 digits")
    @Size(min = 6, max = 6)
    @Column(name = "pincode")
    private String pincode;

    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Set<User> users = new HashSet<>();
}