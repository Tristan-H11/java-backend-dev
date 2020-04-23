package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

import java.util.function.Consumer;

public class DaemonServerCodec extends NettyCodec<DaemonCodecServerInitializer> {

    private final DaemonHandler daemonHandler;

    public DaemonServerCodec(final DaemonHandler daemonHandler, final DaemonEndpointHandler daemonEndpointHandler) {
        super(new DaemonCodecServerInitializer());
        this.daemonHandler = daemonHandler;
        daemonEndpointHandler.getApiList().getClientList().registerClientRemovedCallback((apiClient -> daemonHandler.removeDaemon(apiClient.getChannel())));
        this.setChildCodecs(new JsonApiServerCodec(new ApiEndpointFinder(daemonEndpointHandler.getApiList()), daemonEndpointHandler.getApiList().getClientList(), this::handleResponse));
    }

    private void handleResponse(final JsonObject jsonObject) {
        this.daemonHandler.respondToClient(jsonObject);
    }
}