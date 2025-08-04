package com.kaldi.dto;

import java.io.Serializable;
import java.util.Date;

public record AddMessageDTO(String content, Date timestamp, Long conversationId) implements Serializable {
}
