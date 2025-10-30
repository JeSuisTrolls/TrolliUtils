package fr.jesuistrolls.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;

public class TransfertManager {

    public static boolean Transfer(String oldAccount, String newAccount) {

        OfflinePlayer contextPlayer = Bukkit.getOfflinePlayer(oldAccount);
        record TransferEntry(String type, double value) {}

        Map<String, String> placeholders = Map.ofEntries(
                Map.entry("gemme", "%squidcustomeconomy_balance_gemme%"),
                Map.entry("gemme_compte", "%squidcustomeconomy_balance_gemme_compte%"),
                Map.entry("key_fragment", "%squidcustomeconomy_balance_key_fragment%"),
                Map.entry("default", "%vault_eco_balance%"),
                Map.entry("crane", "%beasttokens_tokens%"),
                Map.entry("hunter", "%jobs_user-jlevel_chasseur%"),
                Map.entry("fishman", "%jobs_user-jlevel_pecheur%"),
                Map.entry("farmer", "%jobs_user-jlevel_agriculteur%"),
                Map.entry("explorer", "%jobs_user-jlevel_explorateur%"),
                Map.entry("minor", "%jobs_user-jlevel_mineur%"),
                Map.entry("lumberjack", "%jobs_user-jlevel_bucheron%"),
                Map.entry("personnage", "%playerlevel_level%"),
                Map.entry("rank", "%luckperms_last_group_on_tracks_adventure%"),
                Map.entry("grade", "%luckperms_highest_group_by_weight%"),
                Map.entry("premium_new", "%luckperms_expiry_time_group.premium_new%")
        );

        String premium_new = PlaceholderAPI.setPlaceholders(contextPlayer, "%luckperms_expiry_time_group.premium_new%").replace(" ", "");
        String crane = PlaceholderAPI.setPlaceholders(contextPlayer, "%beasttokens_tokens%").replace(",", "");
        String allGroups = PlaceholderAPI.setPlaceholders(contextPlayer, "%luckperms_groups%").replace(" ", "");


        List<TransferEntry> entries = placeholders.entrySet().stream()
                .map(e -> new TransferEntry(e.getKey(), parseDouble(PlaceholderAPI.setPlaceholders(contextPlayer, e.getValue()))))
                .toList();

        boolean hasTransferred = false;

        for (TransferEntry entry : entries) {
            if (entry.value() > 0) {
                TransfertEcoCommand(oldAccount, newAccount, entry.type(), entry.value());
                hasTransferred = true;
            }
        }
        if (!premium_new.isEmpty()) {
            TransfertPremiumCommand(oldAccount, newAccount, premium_new);
        }
        if (!allGroups.isEmpty()) {
            String[] groups = allGroups.split(",");
            for (String group : groups) {
                if (!group.equalsIgnoreCase("default")
                        && !group.equalsIgnoreCase("premium")
                        && !group.equalsIgnoreCase("premium_new")) {
                    TransfertRanksCommand(oldAccount, newAccount, group);
                }
            }
        }


        return hasTransferred;
    }


    public static double parseDouble(String input) {
        try {
            return Double.parseDouble(input.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    record TransferCommand(String take, String give) {}

    public static final Map<String, TransferCommand> ECO_COMMANDS = Map.ofEntries(
            Map.entry("gemme", new TransferCommand("squidceco take %s gemme %.2f", "squidceco give %s gemme %.2f")),
            Map.entry("gemme_compte", new TransferCommand("squidceco take %s gemme_compte %.2f", "squidceco give %s gemme_compte %.2f")),
            Map.entry("key_fragment", new TransferCommand("squidceco take %s key_fragment %.2f", "squidceco give %s key_fragment %.2f")),
            Map.entry("default", new TransferCommand("eco take %s %.2f", "eco give %s %.2f")),
            Map.entry("crane", new TransferCommand("cradmin remove %s %.2f", "cradmin give %s %.2f")),
            Map.entry("hunter", new TransferCommand("jobs stats %s Chasseur takelevel %d", "jobs stats %s Chasseur setlevel %d")),
            Map.entry("fishman", new TransferCommand("jobs stats %s Pecheur takelevel %d", "jobs stats %s Pecheur setlevel %d")),
            Map.entry("farmer", new TransferCommand("jobs stats %s Agriculteur takelevel %d", "jobs stats %s Agriculteur setlevel %d")),
            Map.entry("explorer", new TransferCommand("jobs stats %s Explorateur takelevel %d", "jobs stats %s Explorateur setlevel %d")),
            Map.entry("minor", new TransferCommand("jobs stats %s Mineur takelevel %d", "jobs stats %s Mineur setlevel %d")),
            Map.entry("lumberjack", new TransferCommand("jobs stats %s Bucheron takelevel %d", "jobs stats %s Bucheron setlevel %d")),
            Map.entry("personnage", new TransferCommand("playerlevel xptake %s %s", "playerlevel xpgive %s %s"))
    );

    public static void TransfertEcoCommand(String oldAccount, String newAccount, String type, double amount) {
        TransferCommand command = ECO_COMMANDS.get(type);

        if (command == null) {
            Bukkit.getLogger().warning("No transfer command for type " + type);
            return;
        }

        boolean isJobs = type.equals("hunter") || type.equals("fishman") || type.equals("farmer") ||
                type.equals("explorer") || type.equals("minor") || type.equals("lumberjack");

        String takeCommand;
        String giveCommand;

        if (type.equals("personnage")) {
            takeCommand = command.take().formatted(oldAccount, (int) ((amount - 1) * 100));
            giveCommand = command.give().formatted(newAccount, (int) (amount * 100));
        } else if (isJobs) {
            takeCommand = command.take().formatted(oldAccount, (int) amount);
            giveCommand = command.give().formatted(newAccount, (int) amount);
        } else {
            takeCommand = command.take().formatted(oldAccount, amount);
            giveCommand = command.give().formatted(newAccount, amount);
        }

        dispatch(takeCommand);
        dispatch(giveCommand);

    }

    public static void TransfertRanksCommand(String oldAccount, String newAccount, String rangName) {

        String takeCommand = "lp user %s permission clear".formatted(oldAccount);
        String giveCommand = "lp user %s parent add %s".formatted(newAccount, rangName);

        dispatch(takeCommand);
        dispatch(giveCommand);

    }

    public static void TransfertPremiumCommand(String oldAccount, String newAccount, String timeLeft) {

        String takeCommand = "lp user %s permission unsettemp group.premium_new".formatted(oldAccount);
        String giveCommand = "lp user %s permission settemp group.premium_new true %s".formatted(newAccount, timeLeft);

        dispatch(takeCommand);
        dispatch(giveCommand);

    }

    public static void dispatch(String command) {
        if (command != null && !command.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
