package com.kaldi.dto;

import java.io.Serializable;

public record StartConversationDTO(String roomName, String message) implements Serializable {
}
