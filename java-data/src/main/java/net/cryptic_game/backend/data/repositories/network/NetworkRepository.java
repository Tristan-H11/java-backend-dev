package net.cryptic_game.backend.data.repositories.network;

import net.cryptic_game.backend.data.entities.device.Device;
import net.cryptic_game.backend.data.entities.network.Network;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NetworkRepository extends JpaRepository<Network, UUID> {

    Optional<Network> findByName(String name);

    List<Network> findAllByOwner(Device owner);

    List<Network> findAllByIsPublicIsTrue();
}
