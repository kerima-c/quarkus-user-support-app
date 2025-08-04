package com.kaldi.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Message {
    private Long id;
    private User sender;
    private String content;
    private Date timestamp;
    private Conversation conversation;

    public Message(User sender, String content, Date timestamp, Conversation conversation) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.conversation = conversation;
    }

    public Message() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
