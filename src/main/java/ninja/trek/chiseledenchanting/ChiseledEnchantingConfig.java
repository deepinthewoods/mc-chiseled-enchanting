package ninja.trek.chiseledenchanting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChiseledEnchantingConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("chiseled-enchanting.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ChiseledEnchantingConfig INSTANCE;

    public boolean inverseWeightingForBooks = true;
    public boolean inverseWeightingForItems = false; // Default value

    public boolean enableWeighting = true;

    public static ChiseledEnchantingConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(Files.readString(CONFIG_PATH), ChiseledEnchantingConfig.class);
            } else {
                INSTANCE = new ChiseledEnchantingConfig();
                save();
            }
        } catch (Exception e) {
            ChiseledEnchanting.LOGGER.error("Failed to load config", e);
            INSTANCE = new ChiseledEnchantingConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            ChiseledEnchanting.LOGGER.error("Failed to save config", e);
        }
    }
}