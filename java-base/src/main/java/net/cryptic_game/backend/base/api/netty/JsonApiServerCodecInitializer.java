package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonMessageCodec;
import net.cryptic_game.backend.base.netty.codec.MessageLoggerCodec;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@Getter(AccessLevel.NONE)
public final class JsonApiServerCodecInitializer extends NettyCodecInitializer<JsonApiServerCodec> {

    private final Map<String, ApiEndpointData> endpoints;
    private final Set<ApiClient> clients;
    private final BiConsumer<JsonObject, ApiClient> responseCallback;
    private final Consumer<ApiClient> connectedCallback;
    private final Consumer<ApiClient> disconnectedCallback;

    @Override
    public void configure(final ChannelPipeline pipeline) {
        if (!AppBootstrap.getInstance().getConfig().isProductive()) {
            pipeline.addLast(new MessageLoggerCodec());
        }
        pipeline.addLast("json-codec", new JsonMessageCodec());
        pipeline.addLast("json-api-handler", new JsonApiServerContentHandler(this.endpoints, this.clients,
                this.responseCallback, this.connectedCallback, this.disconnectedCallback));
    }
}
