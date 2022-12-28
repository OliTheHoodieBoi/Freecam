package lunarfreecam.freecam;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;

public class Handler implements Listener {

    private final Main plugin;

    public Handler(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Stop player in freecam mode from using spectator teleport
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE) && Main.npcs.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.message("freecam-illegal"));
            event.setCancelled(true);
        }
    }

    /**
     * Stop player in freecam mode from spectating entities
     * @param event event
     */
    @EventHandler
    public void onPlayerStartSpectating(PlayerStartSpectatingEntityEvent event) {
        Player player = event.getPlayer();
        if (Main.npcs.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.message("freecam-illegal"));
            event.setCancelled(true);
        }
    }

    /**
     * Stop Freecam on any damage!
     *
     * @param event event
     */
    @EventHandler
    public void onFreecamDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        LivingEntity victim = (LivingEntity) event.getEntity();
        if (Main.npcs.containsValue(victim)) {
            Player player = Bukkit.getPlayer(getKey(Main.npcs, victim));
            assert player != null;
            NpcManager.exitFreecam(player);
            player.sendMessage(plugin.message("freecam-cancelled"));
            event.setCancelled(true);
        }
    }

    /**
     * Return player to the last location if he leaves the game!
     *
     * @param event event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Main.npcs.containsKey(player.getUniqueId()))
            NpcManager.exitFreecam(player);
    }

    /**
     * Cancel freecam if player changes gamemode
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
        if (event.getCause().equals(PlayerGameModeChangeEvent.Cause.PLUGIN))
            return;
        Player player = event.getPlayer();
        if (Main.playersInFreecam.contains(player))
            NpcManager.cancelFreecam(player);
    }

    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (entry.getValue().equals(value))
                return entry.getKey();
        return null;
    }
}
