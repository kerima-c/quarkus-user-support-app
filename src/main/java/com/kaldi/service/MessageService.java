package com.kaldi.service;

import com.kaldi.model.Conversation;
import com.kaldi.model.Message;
import com.kaldi.model.User;
import com.kaldi.repository.MessageRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class MessageService {
    @Inject
    UserService userService;
    @Inject
    ConversationService conversationService;
    @Inject
    MessageRepository messageRepository;
    @Inject
    SecurityIdentity securityIdentity;

    public List<Message> getMessagesForConversation(Long conversationId) {
        return messageRepository.getMessagesForConversation(conversationId);
    }
    public void createMessage(Conversation conversation, String content, Date timestamp) {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);

        messageRepository.createMessage(user, conversation, content, timestamp);
    }
}
