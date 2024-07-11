package cz.foresttech.commandapi.paper.argument;

import cz.foresttech.commandapi.shared.ArgumentTypeProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerArgumentProcessor implements ArgumentTypeProcessor<Player> {

    @Override
    public Player get(String argument) {
        Player player = Bukkit.getPlayer(argument);
        if (player == null) {
            try {
                UUID uuid = UUID.fromString(argument);
                player = Bukkit.getPlayer(uuid);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return player;
    }

    @Override
    public List<String> tabComplete(String argument) {
        //TODO?
        return null;
    }

}
