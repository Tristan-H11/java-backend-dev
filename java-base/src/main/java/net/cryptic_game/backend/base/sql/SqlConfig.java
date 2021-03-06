package net.cryptic_game.backend.base.sql;

import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.config.Config;
import org.springframework.beans.factory.annotation.Value;

@Config
@Getter(AccessLevel.PACKAGE)
class SqlConfig {

    @Value("${SQL_SERVER_LOCATION:postgresql://localhost:5432}")
    private String location;

    @Value("${SQL_SERVER_DATABASE:cryptic}")
    private String database;

    @Value("${SQL_SERVER_USERNAME:cryptic}")
    private String username;

    @Value("${SQL_SERVER_PASSWORD:cryptic}")
    private String password;

    @Value("${SQL_SHOW_STATEMENTS:false}")
    private boolean showStatements;
}
