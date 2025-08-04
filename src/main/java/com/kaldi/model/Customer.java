package com.kaldi.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User {
    public Customer() {
        this.setUserType(UserType.CUSTOMER);
    }
}
