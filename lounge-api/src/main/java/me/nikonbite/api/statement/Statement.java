package me.nikonbite.api.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Statement {

    LOBBY,
    QUEUE,
    EDITOR,
    PARTY,
    MATCH,
    PARTY_MATCH,
    SPECTATOR,
    FFA
    ;
}
