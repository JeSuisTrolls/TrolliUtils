package fr.jesuistrolls.managers;

import fr.jesuistrolls.TrolliUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class TransfertManager {

    public static boolean Transfer(String oldAccount, String newAccount) {
        ConfigurationSection transfertSection = TrolliUtils.getInstance()
                .getConfig()
                .getConfigurationSection("transfert.placeholder-to-transfert");

        if (transfertSection == null) {
            Bukkit.getLogger().warning("[TrolliUtils] Aucune configuration de transfert trouvée!");
            return false;
        }

        OfflinePlayer oldPlayer = Bukkit.getOfflinePlayer(oldAccount);
        boolean hasTransferred = false;

        for (String key : transfertSection.getKeys(false)) {
            ConfigurationSection placeholderConfig = transfertSection.getConfigurationSection(key);

            if (placeholderConfig == null) continue;

            String placeholder = placeholderConfig.getString("placeholder");

            if (key.equalsIgnoreCase("groups")) {
                handleGroupsTransfer(oldAccount, newAccount, placeholderConfig);
                hasTransferred = true;
                continue;
            }

            if (placeholder == null || placeholder.isEmpty()) continue;

            String rawValue = PlaceholderAPI.setPlaceholders(oldPlayer, placeholder);



            double value = parseDouble(rawValue);
            if (value > 0.9) {
                executeTransferCommands(
                        oldAccount,
                        newAccount,
                        value,
                        placeholderConfig
                );
                hasTransferred = true;
            }
        }

        return hasTransferred;
    }

    private static void handleGroupsTransfer(String oldAccount, String newAccount,
                                             ConfigurationSection config) {
        List<String> oldCommands = config.getStringList("transfert-commands-old-account");
        for (String cmd : oldCommands) {
            String command = cmd
                    .replace("%old_account%", oldAccount)
                    .replace("%new_account%", newAccount);
            dispatch(command);
        }
    }

    private static void executeTransferCommands(String oldAccount, String newAccount,
                                                double value, ConfigurationSection config) {
        int valueInt = (int) value;

        int valueXpOld = (int) ((value - 1) * 100);
        int valueXpNew = (int) (value * 100);

        List<String> oldCommands = config.getStringList("transfert-commands-old-account");
        for (String cmd : oldCommands) {
            String command = cmd
                    .replace("%old_account%", oldAccount)
                    .replace("%value%", String.format("%.2f", value))
                    .replace("%value_int%", String.valueOf(valueInt))
                    .replace("%value_xp_old%", String.valueOf(valueXpOld));
            dispatch(command);
        }

        List<String> newCommands = config.getStringList("transfert-commands-new-account");
        for (String cmd : newCommands) {
            String command = cmd
                    .replace("%new_account%", newAccount)
                    .replace("%value%", String.format("%.2f", value))
                    .replace("%value_int%", String.valueOf(valueInt))
                    .replace("%value_xp_new%", String.valueOf(valueXpNew));
            dispatch(command);
        }
    }

    private static double parseDouble(String input) {
        if (input == null || input.isEmpty()) return 0;

        try {
            return Double.parseDouble(input.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void dispatch(String command) {
        if (command != null && !command.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}