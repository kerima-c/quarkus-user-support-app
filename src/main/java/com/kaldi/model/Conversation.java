package com.kaldi.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "conversation")
public class Conversation {
    private Long id;
    private Customer customer;
    private Operator operator;
    private Room room;
    private Status status;
    private Date createdAt;
    private Date takenAt;
    private Date resolvedAt;

    public Conversation(Customer customer, Operator operator, Room room, Status status, Date createdAt, Date takenAt, Date resolvedAt) {
        this.customer = customer;
        this.operator = operator;
        this.room = room;
        this.status = status;
        this.createdAt = createdAt;
        this.takenAt = takenAt;
        this.resolvedAt = resolvedAt;
    }

    public Conversation() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "operator_id", nullable = true)
    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "taken_at")
    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    @Column(name = "resolved_at")
    public Date getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Date resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
