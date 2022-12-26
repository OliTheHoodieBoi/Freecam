package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import FreecamUtils.utils;
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("freecam-illegal")));
            event.setCancelled(true);
        }
        if (!Main.playersInFreecam.contains(event.getPlayer()) && Main.playersInFreecam.stream().anyMatch(c -> c.getLocation().getWorld().equals(event.getTo().getWorld()) && c.getLocation().distance(event.getTo()) < 0.1)) {
            event.getPlayer().sendMessage(utils.Color(plugin.getConfig().getString("freecam-illegal")));
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
            player.sendMessage(utils.Color(plugin.getConfig().getString("freecam-illegal")));
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
            event.setCancelled(true);
            Player player = Bukkit.getPlayer(getKey(Main.npcs, victim));
            assert player != null;
            NpcManager.exitFreecam(player);
        }
    }

    /**
     * Stop Player from Interact with inventories
     */
    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent e) {
        if (Main.npcs.containsKey(e.getPlayer().getUniqueId())) {
            if (!e.getInventory().getType().equals(InventoryType.PLAYER)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(utils.Color(plugin.getConfig().getString("freecam-cant-open-inv")));
            }
        }
    }

    /**
     * Stop Player from Interacts
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (Main.npcs.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    /**
     * Stop Player from DropItem
     */
    @EventHandler
    public void onPlayerDropItems(PlayerDropItemEvent e) {
        if (Main.npcs.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(utils.Color(plugin.getConfig().getString("freecam-cant-drop-items")));
        }
    }

    /**
     * Return player to the last location if he leaves the game!
     *
     * @param event event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (Main.npcs.containsKey(player.getUniqueId()))
            NpcManager.exitFreecam(player);
    }

    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (entry.getValue().equals(value))
                return entry.getKey();
        return null;
    }
}
