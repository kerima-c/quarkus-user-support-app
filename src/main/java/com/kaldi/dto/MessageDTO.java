package com.kaldi.dto;

import java.io.Serializable;
import java.util.Date;

public record MessageDTO (String content, Date timestamp, String userType) implements Serializable {
}
