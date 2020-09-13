package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.DaemonEndpointData;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.HttpClientUtils;
import net.cryptic_game.backend.data.redis.entities.Session;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * No {@link net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection}!
 */
@Slf4j
@RequiredArgsConstructor
public final class WebSocketDaemonEndpoints {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String AUTHORIZATION_HEADER = HttpHeaderNames.AUTHORIZATION.toString();
    private final String apiToken;

    public ApiResponse send(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                            @ApiParameter(value = "tag", special = ApiParameterSpecialType.TAG) final String tag,
                            @ApiParameter(value = "endpoint", special = ApiParameterSpecialType.ENDPOINT) final ApiEndpointData endpoint,
                            @ApiParameter(value = "data", special = ApiParameterSpecialType.DATA) final JsonObject data) {

        final Session session = client.get(Session.class);

        if (session == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_SESSION");
        }

        if (endpoint instanceof DaemonEndpointData) {
            final DaemonEndpointData daemonEndpoint = (DaemonEndpointData) endpoint;
            final Daemon daemon = daemonEndpoint.getDaemon();
            final String url = daemon.getUrl() + "/" + endpoint.getName();

            HttpClientUtils.sendAsyncRequest(new Request.Builder()
                    .url(url)
                    .header(AUTHORIZATION_HEADER, this.apiToken)
                    .post(RequestBody.create(JsonBuilder.create(data).add("user_id", session.getUserId()).build().toString(), HttpClientUtils.JSON))
                    .build(), new HttpClientUtils.ApiCallback() {

                @Override
                public void onFailure(final @NotNull IOException e) {
                    log.warn("Unable to connect to daemon {} at {}: {}", daemon.getName(), url, e.getMessage());
                    client.getChannel().writeAndFlush(new TextWebSocketFrame(Unpooled.copiedBuffer(
                            new ApiResponse(ApiResponseType.BAD_GATEWAY, "DAEMON_UNAVAILABLE", null, tag).serialize().toString(), CHARSET)));
                }

                @Override
                public void onResponse(final @NotNull ApiResponse response) {
                    response.setTag(tag);
                    client.getChannel().writeAndFlush(new TextWebSocketFrame(Unpooled.copiedBuffer(response.serialize().toString(), CHARSET)));
                }
            });
            return null;

        } else {
            log.warn("Method {}.send(...) was executed with wrong endpoint {}.", WebSocketDaemonEndpoints.class.getName(), endpoint.getName());
            return new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }
    }
}
