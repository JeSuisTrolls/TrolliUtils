package fr.jesuistrolls.commands.player;

import fr.jesuistrolls.configurations.Messages;
import fr.jesuistrolls.managers.TransfertManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransfertCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            Messages.PLAYER_ONLY.send(sender);
            return true;
        }
        if(!player.hasPermission(Messages.TRANSFERT_PERMISSION.getString())) {
            Messages.NO_PERMISSION.send(sender);
            return true;
        }

        if(args.length < 2) {
            Messages.sendBrute(sender, "<red>Usage: /transfert <old_account> <new_account>");
            return true;
        }

        String oldAccount = args[0];
        String newAccount = args[1];

        OfflinePlayer oldPlayer = Bukkit.getOfflinePlayer(oldAccount);
        OfflinePlayer newPlayer = Bukkit.getOfflinePlayer(newAccount);

        if (!oldPlayer.hasPlayedBefore() && !oldPlayer.isOnline()) {
            Messages.sendBrute(sender, "<red>Le compte <white>" + oldAccount + "</white> n'existe pas ou n'a jamais joué sur ce serveur.");
            return true;
        }

        if (!newPlayer.hasPlayedBefore() && !newPlayer.isOnline()) {
            Messages.sendBrute(sender, "<red>Le compte <white>" + newAccount + "</white> n'existe pas ou n'a jamais joué sur ce serveur.");
            return true;
        }

        if(newPlayer.equals(oldPlayer)) {
            Messages.sendBrute(sender, "<red>Vous ne pouvez pas transférer sur le même compte débile.");
            return true;
        }


        try {
            boolean success = TransfertManager.Transfer(oldAccount, newAccount);
            if (success) {
                Messages.TRANSFERT_SUCCESS.send(sender);
                Messages.TRANSFERT_SUCCESS_TARGET.send((CommandSender) newPlayer);
            } else {
                Messages.ERROR_COMMAND.send(sender);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            Messages.sendBrute(sender, "<red>Une erreur est survenue lors du transfert.");
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tempArgs = new ArrayList<>();
        if (args.length == 1 || args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    tempArgs.add(player.getName());
                }
            }
            return tempArgs;
        }
        return List.of();
    }
}
