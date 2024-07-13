package cz.foresttech.commandapi.velocity;

import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Interface for providing {@link ProxyServer} instance to ForestCommandAPI methods.
 * Main class of the Velocity plugin should implement this interface.
 */
public interface ProxyServerProvider {

    ProxyServer getProxyServer();

}
