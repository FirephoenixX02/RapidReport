package me.firephoenix.rapidreport.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import me.firephoenix.rapidreport.RapidReport;
import me.firephoenix.rapidreport.ui.CustomInventory;
import me.firephoenix.rapidreport.ui.UIManager;
import me.firephoenix.rapidreport.utils.Report;
import me.firephoenix.rapidreport.ui.UIComponent;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class ReportGUICommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource commandSource = invocation.source();

        if (!(commandSource instanceof Player)) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>This command can only be executed by a player.");
            return;
        }

        // Fetch all unresolved reports from the database
        CompletableFuture<ResultSet> future = RapidReport.INSTANCE.getDataBaseManager().getSQLStatementResultAsync(
                "SELECT * FROM rapid_report_reports WHERE status = 'Unresolved' LIMIT 36");

        future.thenAccept(result -> {
            ArrayList<Report> reports = new ArrayList<>();
            try {
                while (result.next()) {
                    reports.add(new Report(result.getString("reporterName"), result.getString("reportedName"),
                            UUID.fromString(result.getString("reportedUUID")), result.getString("reason"),
                            result.getString("status"), result.getInt("id")));
                }
                result.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Open the inventory for the player
            ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(((Player) commandSource).getUniqueId());
            protocolizePlayer.openInventory(RapidReport.INSTANCE.getUiManager().createReportListGUI(reports, protocolizePlayer));
        }).exceptionally(ex -> {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Error fetching reports.");
            return null;
        });
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("rapidreport.gui");
    }

}
