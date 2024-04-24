package me.firephoenix.rapidreport.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.firephoenix.rapidreport.RapidReport;


/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class CloseReportCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource commandSource = invocation.source();

        String[] args = invocation.arguments();

        if (args.length != 1) {
            commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Please provide the ID of the report which should be closed.");
            return;
        }

        int id = Integer.parseInt(args[0]);

        RapidReport.INSTANCE.getDataBaseManager().runStatementAsync("UPDATE rapid_report_reports SET status = 'Resolved' WHERE id = " + id);

        commandSource.sendRichMessage(RapidReport.INSTANCE.getChatPrefix() + "<red>Closed Report with ID <gray>" + id);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("rapidreport.closereports");
    }

}
