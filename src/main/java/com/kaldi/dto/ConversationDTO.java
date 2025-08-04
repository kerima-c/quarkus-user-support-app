package com.kaldi.dto;

import java.io.Serializable;
import java.util.Date;

public record ConversationDTO(String customer, String roomName, Date createdAt) implements Serializable {
}
