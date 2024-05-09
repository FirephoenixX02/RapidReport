package me.firephoenix.rapidreport.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import me.firephoenix.rapidreport.RapidReport;
import me.firephoenix.rapidreport.utils.DataBaseManager;
import me.firephoenix.rapidreport.utils.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class ReportCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource commandSource = invocation.source();

        String reporterName = commandSource instanceof Player player ? player.getUsername() : "console";

        String[] args = invocation.arguments();

        if (args.length == 0) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>No arguments provided!");
            return;
        }

        Player reportedPlayer = RapidReport.INSTANCE.getProxy().getPlayer(args[0]).get();

        UUID reportedPlayerUUID = reportedPlayer.getUniqueId();

        if (RapidReport.INSTANCE.getProxy().getPlayer(args[0]).isEmpty()) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>The player <gray>" + args[0] + "<red> is not online!");
            return;
        }

        if (args.length == 1) {
            //GUI
            if (!(commandSource instanceof Player)) {
                commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>This command can only be executed by a player!");
            }
            assert commandSource instanceof Player;
            ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(((Player) commandSource).getUniqueId());
            protocolizePlayer.openInventory(RapidReport.INSTANCE.getUiManager().createReportingGUI((Player) commandSource, reportedPlayer, reportedPlayerUUID, protocolizePlayer));
            return;
        }

        if (args.length != 2) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Please enter in a player you want to report and a reason.");
            return;
        }

        if (RapidReport.INSTANCE.getProxy().getPlayer(args[0]).isEmpty()) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>The player <gray>" + args[0] + "<red> is not online!");
            return;
        }

        Report report = new Report(reporterName, reportedPlayer.getUsername(), reportedPlayerUUID, args[1], "Unresolved");

        RapidReport.INSTANCE.getDataBaseManager().submitNewReportToDB(report);

        commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Player <gray>" + reportedPlayer.getUsername() + "<red> for <gray>" + args[1]);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("rapidreport.report");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        List<String> playerNames = new ArrayList<>();
        RapidReport.INSTANCE.getProxy().getAllPlayers().forEach(player -> playerNames.add(player.getUsername()));
        return CompletableFuture.completedFuture(playerNames);
    }
}
