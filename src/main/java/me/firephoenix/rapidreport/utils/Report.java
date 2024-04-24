package me.firephoenix.rapidreport.utils;

import me.firephoenix.rapidreport.commands.ReportCommand;

import java.util.UUID;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class Report {
    public final String reporterPlayerName;
    public final String reportedPlayerName;
    public final UUID reportedPlayerUUID;
    public final String reason;
    public final String status;

    public Report(final String reporterPlayerName, final String reportedPlayerName, final UUID reportedPlayerUUID, final String reason, final String status) {
        this.reporterPlayerName = reporterPlayerName;
        this.reportedPlayerName = reportedPlayerName;
        this.reportedPlayerUUID = reportedPlayerUUID;
        this.reason = reason;
        this.status = status;
    }

}
