package cz.foresttech.commandapi.velocity.argument;

import com.velocitypowered.api.proxy.Player;
import cz.foresttech.commandapi.shared.AbstractCommandSenderWrapper;
import cz.foresttech.commandapi.shared.processor.ArgumentTypeProcessor;
import cz.foresttech.commandapi.velocity.CommandAPI;

import java.util.List;
import java.util.UUID;

public class PlayerArgumentProcessor implements ArgumentTypeProcessor<Player> {

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> Player get(S commandSender, String argument) {
        Player player = CommandAPI.getProxyServer().getPlayer(argument).orElse(null);
        if (player == null) {
            try {
                UUID uuid = UUID.fromString(argument);
                player = CommandAPI.getProxyServer().getPlayer(uuid).orElse(null);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return player;
    }

    @Override
    public <S extends AbstractCommandSenderWrapper<?>> List<String> tabComplete(S commandSender, String argument) {
        String inLowerCase = argument.toLowerCase();
        CommandAPI.getProxyServer().getAllPlayers();
        return CommandAPI.getProxyServer().getAllPlayers().stream()
                .map(Player::getUsername)
                .filter(name -> name.toLowerCase().startsWith(inLowerCase))
                .toList();
    }

}
