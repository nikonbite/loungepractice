package me.nikonbite.practice.kit.editor;

import lombok.Getter;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EditorSession {
    private final Map<LoungePlayer, Kit> sessions = new HashMap<>();

    public void addToSession(LoungePlayer player, Kit kit) {
        sessions.put(player, kit);
    }

    public void removeFromSession(LoungePlayer player) {
        sessions.remove(player);
    }
}
