package net.cryptic_game.backend;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.handler.rest.RestApiAuthenticator;
import net.cryptic_game.backend.base.api.handler.rest.RestApiRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class DaemonAuthenticator implements RestApiAuthenticator {

    private final BaseConfig config;
    private final boolean authentication;

    public DaemonAuthenticator(final Bootstrap bootstrap,
                               final BaseConfig config) {
        this.config = config;
        this.authentication = this.config.getApiToken() != null && !this.config.getApiToken().isBlank();

        if (!this.authentication) {
            log.warn("No Api token was specified, endpoints can be accessed without authentication.");

            if (!bootstrap.isDebug()) {
                log.error("The Daemon cannot be started in production mode without a api token.");
                bootstrap.shutdown();
            }
        }
    }

    @Override
    public boolean isPermitted(final RestApiRequest request, final int authentication, final ApiEndpointData endpoint) {
        return !this.authentication || this.config.getApiToken().equals(request.getContext().getHttpRequest().requestHeaders().get(HttpHeaderNames.AUTHORIZATION));
    }
}
