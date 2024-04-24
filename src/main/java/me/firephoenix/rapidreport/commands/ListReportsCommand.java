package me.firephoenix.rapidreport.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.firephoenix.rapidreport.RapidReport;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class ListReportsCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource commandSource = invocation.source();

        String[] args = invocation.arguments();

        if (args.length == 0) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Fetching first 10 reports from database...");

            CompletableFuture<ResultSet> future = RapidReport.INSTANCE.getDataBaseManager().getSQLStatementResultAsync("SELECT * FROM rapid_report_reports WHERE status = 'Unresolved' LIMIT 10");

            future.thenAccept(result -> {
                try {
                    while (result.next()) {
                        commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<gray>[<red>" + result.getString("id") + "<gray>] <red>Reported Player: <gray>" + result.getString("reportedName") + " <red> Reason: <gray>" + result.getString("reason"));
                    }
                    result.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            if (args.length != 1) {
                commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Please provide the name of the player you want to get reports for.");
                return;
            }

            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Fetching first 10 reports for player <gray>" + args[0] +   "<red> from database...");

            CompletableFuture<ResultSet> future = RapidReport.INSTANCE.getDataBaseManager().getSQLStatementResultAsync("SELECT * FROM rapid_report_reports WHERE reportedName = '" + args[0] + "' LIMIT 10");

            future.thenAccept(result -> {
                try {
                    while (result.next()) {
                        commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<gray>[<red>" + result.getString("id") + "<gray>] <red>Reported Player: <gray>" + result.getString("reportedName") + " <red> Reason: <gray>" + result.getString("reason"));
                    }
                    result.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("rapidreport.reports");
    }

}
