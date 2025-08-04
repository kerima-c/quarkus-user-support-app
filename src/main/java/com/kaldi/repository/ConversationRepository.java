package com.kaldi.repository;

import com.kaldi.model.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ConversationRepository implements PanacheRepository<Conversation> {
    @Inject
    MessageRepository messageRepository;
    public List<Conversation> getPendingConversations() {
        return list("status", Status.PENDING);
    }

    public List<Conversation> getTakenConversations() {
        return list("status", Status.TAKEN);
    }

    public Conversation getConversationById(Long conversationId) {
        return find("id", conversationId).firstResult();
    }

    public List<Conversation> getCustomerConversations(Long customerId) {
        return list("customer.id", customerId);
    }

    @Transactional
    public Conversation createConversation(Customer customer, Room room, Date timestamp) {
        Conversation conversation = new Conversation(customer, null, room, Status.PENDING, timestamp, null, null);
        persist(conversation);

        return conversation;
    }

    @Transactional
    public Conversation takeConversation(Long conversationId, Operator operator) {
        Conversation conversation = getConversationById(conversationId);
        conversation.setOperator(operator);
        conversation.setTakenAt(new Date());
        conversation.setStatus(Status.TAKEN);

        return conversation;
    }


}
