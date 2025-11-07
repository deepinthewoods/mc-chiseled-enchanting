package ninja.trek.chiseledenchanting;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class ChiseledEnchantingDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider((output, registriesFuture) -> new FabricRecipeProvider(output, registriesFuture) {
            @Override
            public RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
                return new RecipeGenerator(registryLookup, exporter) {
                    @Override
                    public void generate() {
                        ShapedRecipeJsonBuilder.create(getRegistryLookup().getOrThrow(RegistryKeys.ITEM), RecipeCategory.DECORATIONS, ChiseledEnchanting.CHISELED_ENCHANTING_TABLE)
                                .pattern(" B ")
                                .pattern("DTD")
                                .pattern("OOO")
                                .input('B', Items.BOOK)
                                .input('D', Items.DIAMOND)
                                .input('T', Items.ENCHANTING_TABLE)
                                .input('O', Items.OBSIDIAN)
                                .criterion(hasItem(Items.ENCHANTING_TABLE),
                                        conditionsFromItem(Items.ENCHANTING_TABLE))
                                .offerTo(getExporter(), Identifier.of(ChiseledEnchanting.MOD_ID, "chiseled_enchanting_table"));
                    }
                };
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