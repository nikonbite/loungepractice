package me.nikonbite.practice.match.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchType {
    UNRANKED("Unranked"),
    RANKED("Ranked"),
    PARTY_VS_PARTY("Party vs Party"),
    PARTY_SPLIT("Party Split"),
    PARTY_FFA("Party FFA"),
    DUEL("Duel")
    ;

    final String name;
}
