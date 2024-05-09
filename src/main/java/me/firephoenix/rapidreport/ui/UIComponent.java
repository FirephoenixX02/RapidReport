package me.firephoenix.rapidreport.ui;

import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.Getter;
import lombok.Setter;
import me.firephoenix.rapidreport.RapidReport;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class UIComponent {
    @Getter
    private ItemStack itemStack;
    @Setter
    private Runnable clickListener;
    @Setter
    private String permission;

    public UIComponent(ItemStack itemStack, String permission) {
        this.itemStack = itemStack;
        this.permission = permission;
    }

    public UIComponent(ItemStack itemStack, Runnable clickListener, String permission) {
        this.itemStack = itemStack;
        this.clickListener = clickListener;
        this.permission = permission;
    }

    public void runClickListener(ProtocolizePlayer protocolizePlayer) {
        if (!hasPermission(protocolizePlayer)) return;
        clickListener.run();
    }

    public boolean hasPermission(ProtocolizePlayer protocolizePlayer) {
        if (RapidReport.INSTANCE.getProxy().getPlayer(protocolizePlayer.uniqueId()).isEmpty()) return false;
        return RapidReport.INSTANCE.getProxy().getPlayer(protocolizePlayer.uniqueId()).get().hasPermission(permission);
    }
}
