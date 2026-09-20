package com.declutter.dclutter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@Getter       // ✅ Replaced @Data
@Setter       // ✅ Replaced @Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ Only use ID for equals/hashcode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include // ✅ Only use ID for equals/hashcode
    private Long userId;

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 120)
    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_addresses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<Address> addresses = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Product> products;
}