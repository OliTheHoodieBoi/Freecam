package lunarfreecam.freecam;

import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Chunk;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class Main extends JavaPlugin {
    public final static HashMap<UUID, LivingEntity> npcs = new HashMap<>();
    public final static ArrayList<Player> playersInFreecam = new ArrayList<>();
    public final static HashMap<UUID, PlayerState> previousState = new HashMap<>();
    public static ArrayList<Chunk> forceLoadedChunks = new ArrayList<>();

    public void onEnable() {
        loadConfig();
        NpcManager.setPlugin(this);
        new Handler(this);

        // Register command
        PluginCommand freecamCommand = getCommand("freecam");
        assert freecamCommand != null;
        freecamCommand.setExecutor(new FreecamCommand(this));

        if (CommodoreProvider.isSupported())
            registerCompletions(freecamCommand);
    }

    /**
     * Return all players to their last locations if server stops or restarts!
     */
    public void onDisable() {
        playersInFreecam.forEach(NpcManager::exitFreecam);
    }

    private void registerCompletions(PluginCommand command) {
        Commodore commodore = CommodoreProvider.getCommodore(this);
        commodore.register(command, literal("freecam")
                .then(literal("reload")));
    }

    public void loadConfig() {
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    public TextComponent message(String key){
        String m =this.getConfig().getString(key);
        if (m == null)
            return Component.text("Could not display message").color(NamedTextColor.RED);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(m);
    }
}
