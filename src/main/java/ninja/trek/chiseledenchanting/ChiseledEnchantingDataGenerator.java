package ninja.trek.chiseledenchanting;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class ChiseledEnchantingDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider((output, registriesFuture) -> new FabricRecipeProvider(output, registriesFuture) {
            @Override
            public void generate(RecipeExporter exporter) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ChiseledEnchanting.CHISELED_ENCHANTING_TABLE)
                        .pattern(" B ")
                        .pattern("DTD")
                        .pattern("OOO")
                        .input('B', Items.BOOK)
                        .input('D', Items.DIAMOND)
                        .input('T', Items.ENCHANTING_TABLE)
                        .input('O', Items.OBSIDIAN)
                        .criterion(FabricRecipeProvider.hasItem(Items.ENCHANTING_TABLE),
                                FabricRecipeProvider.conditionsFromItem(Items.ENCHANTING_TABLE))
                        .offerTo(exporter, Identifier.of(ChiseledEnchanting.MOD_ID, "chiseled_enchanting_table"));
            }
        });

        pack.addProvider((output, registriesFuture) -> new FabricBlockLootTableProvider(output, registriesFuture) {
            @Override
            public void generate() {
                addDrop(ChiseledEnchanting.CHISELED_ENCHANTING_TABLE);
            }
        });
    }
}