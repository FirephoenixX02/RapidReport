package me.firephoenix.rapidreport.ui;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import me.firephoenix.rapidreport.RapidReport;
import me.firephoenix.rapidreport.utils.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class UIManager {

    public CustomInventory createReportListGUI(ArrayList<Report> reports, ProtocolizePlayer protocolizePlayer) {
        CustomInventory inventory = new CustomInventory(InventoryType.GENERIC_9X6);
        inventory.title(ChatElement.ofLegacyText("§cReports"));

        //Top Row of Glass
        for (int i = 0; i < 9; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        // Populate the inventory with reports once they are fetched
        int slot = 10;
        for (Report report : reports) {
            if (slot > 43) break;
            List<ChatElement<?>> lore = new ArrayList<>();
            lore.add(ChatElement.ofLegacyText("§cReported by: §7" + report.reporterPlayerName));
            lore.add(ChatElement.ofLegacyText("§cReason: §7" + report.reason));
            lore.add(ChatElement.ofLegacyText("§cStatus: §7" + report.status));

            ItemStack reportItemStack = new ItemStack(ItemType.PAPER);

            reportItemStack.displayName(ChatElement.ofLegacyText("§c" + report.reportedPlayerName));
            reportItemStack.lore(lore);

            UIComponent reportComponent = new UIComponent(reportItemStack, "rapidreport.gui");

            reportComponent.setClickListener(() -> {
                protocolizePlayer.openInventory(createReportEditGUI(report, protocolizePlayer));
            });

            inventory.item(slot, reportComponent);
            slot++;
        }

        // Bottom Row of Glass
        for (int i = 45; i < 54; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        inventory.onClick(click -> {
            if (click.clickType() != ClickType.LEFT_CLICK) return;
            if (inventory.getSlotToComponentMap().containsKey(click.slot())) {
                inventory.getSlotToComponentMap().get(click.slot()).runClickListener(click.player());
            }
        });
        return inventory;
    }

    public CustomInventory createReportEditGUI(Report report, ProtocolizePlayer protocolizePlayer) {
        CustomInventory inventory = new CustomInventory(InventoryType.GENERIC_9X5);
        inventory.title(ChatElement.ofLegacyText("§cEdit-Report"));

        //Top Row of Glass
        for (int i = 0; i < 9; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        ItemStack closeItemStack = new ItemStack(ItemType.BARRIER);

        closeItemStack.displayName(ChatElement.ofLegacyText("§c" + "Close Report"));

        UIComponent closeComponent = new UIComponent(closeItemStack, "rapidreport.gui");

        closeComponent.setClickListener(() -> {
            RapidReport.INSTANCE.getDataBaseManager().closeReport(report);
            protocolizePlayer.closeInventory();
        });

        inventory.item(20, closeComponent);


        // Bottom Row of Glass
        for (int i = 36; i < 45; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        inventory.onClick(click -> {
            if (click.clickType() != ClickType.LEFT_CLICK) return;
            if (inventory.getSlotToComponentMap().containsKey(click.slot())) {
                inventory.getSlotToComponentMap().get(click.slot()).runClickListener(click.player());
            }
        });

        return inventory;
    }

    public CustomInventory createReportingGUI(Player player, Player reportedPlayer, UUID reportedPlayerUUID, ProtocolizePlayer protocolizePlayer) {
        CustomInventory inventory = new CustomInventory(InventoryType.GENERIC_9X5);
        inventory.title(ChatElement.ofLegacyText("§cReport-Player"));

        //Top Row of Glass
        for (int i = 0; i < 9; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        //Load Report Reasons from config and add items
        int slot = 9;
        Map<String, Object> reasons = RapidReport.INSTANCE.getConfig().getTable("report_reasons").toMap();
        for (String key : reasons.keySet()) {
            Map<String, String> reason = (Map<String, String>) reasons.get(key);

            ItemStack itemStack = new ItemStack(ItemType.valueOf(reason.get("item_material")));

            itemStack.displayName(ChatElement.ofLegacyText(reason.get("display_name")));

            UIComponent component = new UIComponent(itemStack, "rapidreport.report");

            Report report = new Report(player.getUsername(), reportedPlayer.getUsername(), reportedPlayerUUID, reason.get("description"), "Unresolved");

            component.setClickListener(() -> {
                RapidReport.INSTANCE.getDataBaseManager().submitNewReportToDB(report);
                reportedPlayer.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Player <gray>" + reportedPlayer.getUsername() + "<red> for <gray>" + reason.get("description"));
                protocolizePlayer.closeInventory();
            });

            inventory.item(slot, component);
            slot++;
            if (slot >= 35) break;
        }

        // Bottom Row of Glass
        for (int i = 36; i < 45; i++) {
            inventory.item(i, new ItemStack(ItemType.GRAY_STAINED_GLASS_PANE));
        }

        inventory.onClick(click -> {
            if (click.clickType() != ClickType.LEFT_CLICK) return;
            if (inventory.getSlotToComponentMap().containsKey(click.slot())) {
                inventory.getSlotToComponentMap().get(click.slot()).runClickListener(click.player());
            }
        });

        return inventory;
    }
}
