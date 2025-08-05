package com.kaldi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPERATOR")
public class Operator extends User {
    public Operator() {
        this.setUserType(UserType.CUSTOMER);
    }
}
