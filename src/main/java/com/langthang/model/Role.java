package com.langthang.model;


import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }

    public boolean isAdmin() {
        return this.name.equals("ROLE_ADMIN");
    }

    public boolean isUser() {
        return this.name.equals("ROLE_USER");
    }
}
