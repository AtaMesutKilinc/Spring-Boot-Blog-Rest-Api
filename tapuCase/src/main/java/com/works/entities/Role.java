package com.works.entities;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; //role ün adı ROLE_user gibi.

//    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    List<Admin> admins;
//
//    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    List<Customer> customers;


}
