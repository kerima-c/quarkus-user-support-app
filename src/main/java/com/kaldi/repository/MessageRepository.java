package com.kaldi.repository;

import com.kaldi.model.Conversation;
import com.kaldi.model.Message;
import com.kaldi.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {

    public List<Message> getMessagesForConversation(Long conversationId) {
        return list("conversation.id", conversationId);
    }

    @Transactional
    public Message createMessage(User user, Conversation conversation, String content, Date timestamp) {
        Message message = new Message(user, content, timestamp, conversation);
        persist(message);

        return message;
    }
}
