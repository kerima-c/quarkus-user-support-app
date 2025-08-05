package com.kaldi.service;

import com.kaldi.model.*;
import com.kaldi.repository.ConversationRepository;
import com.kaldi.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ConversationService {
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    MessageRepository messageRepository;

    public List<Conversation> getPendingConversations() {
        return conversationRepository.getPendingConversations();
    }

    public List<Conversation> getTakenConversations() {
        return conversationRepository.getTakenConversations();
    }

    public Conversation getConversationById(Long conversationId) {
        return conversationRepository.getConversationById(conversationId);
    }

    public List<Conversation> getCustomerConversations(Long customerId) {
        return conversationRepository.getCustomerConversations(customerId);
    }

    public Conversation startConversation(Customer customer, Room room, Date timestamp, String messageContent) {
        Conversation conversation = conversationRepository.createConversation(customer, room, timestamp);
        Message message = messageRepository.createMessage(customer, conversation, messageContent, timestamp);

        return conversation;
    }

    public Conversation takeConversation(Long conversationId, Operator operator) {
        return conversationRepository.takeConversation(conversationId, operator);
    }
}
