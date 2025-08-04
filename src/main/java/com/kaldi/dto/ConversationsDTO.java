package com.kaldi.dto;

import java.io.Serializable;
import java.util.List;

public record ConversationsDTO(List<ConversationDTO> conversationDTO) implements Serializable {
}
