package com.kaldi.dto;

import java.io.Serializable;
import java.util.List;

public record MessagesDTO(Long conversationId, List<MessageDetailsDTO> messageDetailsDTOS) implements Serializable {
}
