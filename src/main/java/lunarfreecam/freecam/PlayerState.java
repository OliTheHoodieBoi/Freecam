package lunarfreecam.freecam;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PlayerState {

    private final GameMode gameMode;
    private final float fallDistance;
    private final Vector velocity;

    public PlayerState(@NotNull Player player) {
        gameMode = player.getGameMode();
        fallDistance = player.getFallDistance();
        velocity = player.getVelocity();
    }

    public void apply(@NotNull Player player) {
        player.setGameMode(gameMode);
        player.setFallDistance(fallDistance);
        player.setVelocity(velocity);
    }
}
