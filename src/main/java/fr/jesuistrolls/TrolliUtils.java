package fr.jesuistrolls;

import fr.jesuistrolls.commands.CommandLoader;
import fr.jesuistrolls.configurations.Messages;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class TrolliUtils extends JavaPlugin {

    private static TrolliUtils instance;
    private static BukkitAudiences audience;

    @Override
    public void onEnable() {
        getLogger().info(">> Initializing config...");
        instance = this;
        saveDefaultConfig();
        loadMessages();
        audience = BukkitAudiences.create(this);

        getLogger().info(">> Initializing Commands...");
        new CommandLoader(this).registerCommands();

    }

    @Override
    public void onDisable() {
        if (audience != null) audience.close();
    }

    public void loadMessages() {
        reloadConfig();
        FileConfiguration messages = getConfig();
        Messages.loadAll(messages);
    }

    public static TrolliUtils getInstance() {
        return instance;
    }

    public static BukkitAudiences getAudience() {
        return audience;
    }
}
