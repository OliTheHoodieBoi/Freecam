package lunarfreecam.freecam;

import FreecamUtils.NpcManager;
import FreecamUtils.utils;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.Map;

public class Handler implements Listener {

    private final Main plugin;
    private final NpcManager npcManager;


    public Handler(Main plugin) {
        this.plugin = plugin;
        this.npcManager = new NpcManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * For commands when player is in freecam mode
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        ArrayList<String> whitelistedCommands = new ArrayList<>(plugin.getConfig().getStringList("freecam-whitelisted-commands"));
        if (Main.npcs.containsKey(player.getUniqueId())) {
            if (!whitelistedCommands.contains(message)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("on-freecam-cmd")));
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_BASS.parseSound(), 100, 0);
                event.setCancelled(true);
            }
        }
    }

    /**
     * Stop player in freecam from using spectator teleport
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE) && Main.npcs.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("freecam-spectate-teleport")));
            event.setCancelled(true);
        }
        if (!Main.playersInFreecam.contains(event.getPlayer()) && Main.playersInFreecam.stream().anyMatch(c -> c.getLocation().getWorld().equals(event.getTo().getWorld()) && c.getLocation().distance(event.getTo()) < 0.1)) {
            event.getPlayer().sendMessage(utils.Color(plugin.getConfig().getString("freecam-tp-while-in-freecam")));
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
        if (Main.npcs.containsKey(player.getUniqueId())) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(Main.npcs.get(player.getUniqueId()).getLocation());
            npcManager.deleteNpc(player);
            Main.npcs.remove(player.getUniqueId());
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
            event.setCancelled(true);
            Player player = Bukkit.getPlayer(getKey(Main.npcs, victim));
            npcManager.exitFreecam(player, FreecamCommand.previousGamemode.get(player));
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
        if (Main.npcs.containsKey(player.getUniqueId())) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(Main.npcs.get(player.getUniqueId()).getLocation());
            npcManager.deleteNpc(player);
            Main.npcs.remove(player.getUniqueId());

        }
    }

    /**
     * Stop chunk from unloading if it contains a freecam npc
     *
     * @param event event
     */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities())
            if (entity instanceof LivingEntity && Main.npcs.containsValue(entity))
                event.setCancelled(true);
    }

    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (entry.getValue().equals(value))
                return entry.getKey();
        return null;
    }
}
