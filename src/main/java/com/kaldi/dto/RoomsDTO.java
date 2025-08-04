package com.kaldi.dto;

import java.io.Serializable;
import java.util.List;

public record RoomsDTO(List<RoomDTO> roomDTOS) implements Serializable {
}
