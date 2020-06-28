package net.cryptic_game.backend.daemon.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@EqualsAndHashCode
public final class DaemonEndpointHandler {

    @Getter
    private final ApiEndpointList apiList;
    private Set<ApiEndpointCollection> apiCollections;

    public DaemonEndpointHandler() {
        this.apiCollections = new HashSet<>();
        this.apiList = new ApiEndpointList(null);
    }

    public void postInit() {
        try {
            this.apiList.setCollections(this.apiCollections);
            this.apiCollections = null;
        } catch (ApiException e) {
            log.error("Unable to register Api-Collections.", e);
        }
    }

    public <T extends ApiEndpointCollection> T addApiCollection(final T apiCollection) {
        if (this.apiCollections != null) {
            this.apiCollections.add(apiCollection);
            return apiCollection;
        } else {
            log.error("It's too late to register any more endpoints.");
            return null;
        }
    }
}
