package com.kaldi.dto;

import java.io.Serializable;
import java.util.Date;

public record ConversationDetailsDTO(Long id, String username, String roomName, String status, Date createdAt,
                                     Date takenAt) implements Serializable {
}
