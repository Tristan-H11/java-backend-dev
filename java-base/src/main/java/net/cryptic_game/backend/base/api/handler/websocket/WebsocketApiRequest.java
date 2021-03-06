package net.cryptic_game.backend.base.api.handler.websocket;

import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cryptic_game.backend.base.api.data.ApiRequest;

@Getter
@ToString
@EqualsAndHashCode
public class WebsocketApiRequest extends ApiRequest {

    private final WebsocketApiContext context;

    public WebsocketApiRequest(final String endpoint, final JsonObject data, final String tag, final WebsocketApiContext context) {
        super(endpoint, data, tag);
        this.context = context;
    }
}
