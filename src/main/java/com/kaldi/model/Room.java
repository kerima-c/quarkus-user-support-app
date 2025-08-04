package com.kaldi.model;

import java.util.List;

public enum Room {
    TECHNOLOGY, SERVICES, CONVERSATION;

    public static final List<Room> ALL_ROOMS = List.of(TECHNOLOGY, SERVICES, CONVERSATION);
}
