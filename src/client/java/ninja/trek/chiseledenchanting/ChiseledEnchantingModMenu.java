package ninja.trek.chiseledenchanting;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.text.Text;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public class ChiseledEnchantingModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        System.out.println("ModMenu Screen is Loading!"); // Debug output
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.chiseled-enchanting.title"));

            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.chiseled-enchanting.category.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("config.chiseled-enchanting.option.enable_weighting"),
                            ChiseledEnchantingConfig.get().enableWeighting)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ChiseledEnchantingConfig.get().enableWeighting = newValue;
                        ChiseledEnchantingConfig.save();
                    })
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("config.chiseled-enchanting.option.inverse_books"),
                            ChiseledEnchantingConfig.get().inverseWeightingForBooks)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> {
                        ChiseledEnchantingConfig.get().inverseWeightingForBooks = newValue;
                        ChiseledEnchantingConfig.save();
                    })
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("config.chiseled-enchanting.option.invert_items"),
                            ChiseledEnchantingConfig.get().inverseWeightingForItems)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> {
                        ChiseledEnchantingConfig.get().inverseWeightingForItems = newValue;
                        ChiseledEnchantingConfig.save();
                    })
                    .build());


            return builder.build();
        };
    }
}