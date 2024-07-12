package cz.foresttech.commandapi.paper.argument;

import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;
import cz.foresttech.commandapi.shared.processor.ArgumentTypeProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerArgumentProcessor implements ArgumentTypeProcessor<Player> {

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> Player get(S commandSender, String argument) {
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
    public <S extends AbstractCommandSenderWrapper<?>> List<String> tabComplete(S commandSender, String argument) {
        String inLowerCase = argument.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(inLowerCase))
                .toList();
    }

}
