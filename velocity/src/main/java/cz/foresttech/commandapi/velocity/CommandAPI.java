package cz.foresttech.commandapi.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import cz.foresttech.commandapi.shared.AbstractCommandAPI;
import cz.foresttech.commandapi.velocity.argument.PlayerArgumentProcessor;

public class CommandAPI extends AbstractCommandAPI<CommandSenderWrapper> {

    private static ProxyServer proxyServer;
    private final ProxyServerProvider proxyServerProvider;

    public CommandAPI(ProxyServerProvider proxyServerProvider) {
        this.proxyServerProvider = proxyServerProvider;
        proxyServer = proxyServerProvider.getProxyServer();
    }

    @Override
    protected void setup() {
        registerArgumentTypeProcessor(Player.class, new PlayerArgumentProcessor());
    }

    @Override
    protected boolean registerToPlatform(String cmdName) {
        CommandManager commandManager = proxyServerProvider.getProxyServer().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(cmdName)
                .plugin(this)
                .build();

        VelocityCommandHandler velocityCommandHandler = new VelocityCommandHandler(cmdName.toLowerCase(), this);
        commandManager.register(commandMeta, velocityCommandHandler);
        return true;
    }

    public static ProxyServer getProxyServer() {
        return proxyServer;
    }
}
