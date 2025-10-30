package fr.jesuistrolls.configurations;

import fr.jesuistrolls.TrolliUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public enum Messages {

    PLAYER_ONLY("player-only"),
    NO_PERMISSION("no-permission"),
    ERROR_COMMAND("error"),
    BELOW_ENABLE("below.enable"),
    BELOW_WORLD_WHITELIST("below.worlds-whitelist"),
    BELOW_PERMISSION("below.permission"),
    BELOW_SUCCESS("below.success"),
    TRANSFERT_ENABLE("transfert.enable"),
    TRANSFERT_PERMISSION("transfert.permission"),
    TRANSFERT_SUCCESS("transfert.success"),
    TRANSFERT_SUCCESS_TARGET("transfert.success-target"),
    LEADERBOARD_ENABLE("leaderboard-rewards.enable"),
    LEADERBOARD_PERMISSION("leaderboard-rewards.permission"),
    LEADERBOARD_SUCCESS("leaderboard-rewards.success");


    private final String path;
    private static Object value;

    Messages(String path) {
        this.path = path;
    }

    public String getString() {
        return (String) value;
    }
    public static List<String> getStringList() {
        return (List<String>) value;
    }
    public static List<String> getStringList(String s) {
        return (List<String>) value;
    }

    public boolean getBoolean() {
        return value instanceof Boolean && (Boolean) value;
    }

    public void load(FileConfiguration config) {
        this.value = config.get(path);
    }

    public static void loadAll(FileConfiguration config) {
        for (Messages msg : values()) {
            msg.load(config);
        }
    }
    public static void sendBrute(CommandSender sender, String message) {
        if (message == null) return;
        TrolliUtils.getAudience()
                .sender(sender)
                .sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public void send(CommandSender sender) {
        String message = getString();
        sendBrute(sender, message);
    }

    public void send(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            send(player);
        }
    }

    public void sendReplace(CommandSender sender, String original, String replacement) {
        if (original == null || replacement == null) return;
        String message = getString().replace(original, replacement);
        sendBrute(sender, message);
    }

    public void sendReplace(CommandSender sender, Map<String, String> replacements) {
        String message = getString();
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }
        sendBrute(sender, message);
    }
}
