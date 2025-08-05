package com.kaldi.dto;

import java.io.Serializable;
import java.util.Date;

public record MessageDetailsDTO(String content, Date timestamp, String username) implements Serializable {
}
