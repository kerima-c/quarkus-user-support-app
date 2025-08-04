package com.kaldi.model;

import jakarta.persistence.Entity;

@Entity
public class Operator extends User {
    public Operator() {
        this.setUserType(UserType.CUSTOMER);
    }
}
