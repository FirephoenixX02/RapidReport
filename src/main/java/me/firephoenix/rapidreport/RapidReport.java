package me.firephoenix.rapidreport;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.firephoenix.rapidreport.commands.CloseReportCommand;
import me.firephoenix.rapidreport.commands.ListReportsCommand;
import me.firephoenix.rapidreport.commands.ReportCommand;
import me.firephoenix.rapidreport.commands.ReportGUICommand;
import me.firephoenix.rapidreport.ui.UIManager;
import me.firephoenix.rapidreport.utils.DataBaseManager;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "rapidreport",
        name = "RapidReport",
        version = "1.0",
        authors = {"NieGestorben"},
        dependencies = {
                @Dependency(id = "protocolize")
        }
)
public class RapidReport {
    public static RapidReport INSTANCE;
    @Getter
    public Logger logger;
    @Getter
    public ProxyServer proxy;
    @Getter
    public Path dataFolderPath;
    @Getter
    public Toml config;
    @Getter
    public DataBaseManager dataBaseManager;
    @Getter
    public String chatPrefix = "<gray>[<red>RapidReport<gray>] ";
    @Getter
    public UIManager uiManager;
    private final Metrics.Factory metricsFactory;

    @Inject
    public RapidReport(ProxyServer proxyServer, Logger logger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        this.proxy = proxyServer;
        this.logger = logger;
        this.dataFolderPath = folder;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();

        setInstance(this);

        config = loadConfig(dataFolderPath);

        dataBaseManager = new DataBaseManager();

        dataBaseManager.initDB();

        uiManager = new UIManager();

        //bStats
        Metrics metrics = metricsFactory.make(this, 21977);

        commandManager.register(commandManager.metaBuilder("report").plugin(this).build(), new ReportCommand());
        commandManager.register(commandManager.metaBuilder("reports").plugin(this).build(), new ListReportsCommand());
        commandManager.register(commandManager.metaBuilder("closereport").plugin(this).build(), new CloseReportCommand());
        commandManager.register(commandManager.metaBuilder("reportgui").plugin(this).build(), new ReportGUICommand());
    }

    public void setInstance(RapidReport INSTANCE) {
        RapidReport.INSTANCE = INSTANCE;
    }

    public Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");

        if (!file.getParentFile().exists()) {
            boolean created = file.getParentFile().mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create directories for config file.");
            }
        }

        if (!file.exists()) {
            try (InputStream inputStream = RapidReport.class.getResourceAsStream("/" + file.getName())) {
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                } else {
                    boolean created = file.createNewFile();
                    if (!created) {
                        throw new RuntimeException("Failed to create config file.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error while loading config file: " + e.getMessage());
            }
        }

        return new Toml().read(file);
    }
}
