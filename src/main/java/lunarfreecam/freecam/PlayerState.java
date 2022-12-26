package lunarfreecam.freecam;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerState {

    private final GameMode gameMode;
    private final float fallDistance;
    private final int fireTicks;

    public PlayerState(@NotNull Player player) {
        gameMode = player.getGameMode();
        fallDistance = player.getFallDistance();
        fireTicks = player.getFireTicks();
    }

    public void apply(@NotNull Player player) {
        player.setGameMode(gameMode);
        player.setFallDistance(fallDistance);
        player.setFireTicks(fireTicks);
    }
}
