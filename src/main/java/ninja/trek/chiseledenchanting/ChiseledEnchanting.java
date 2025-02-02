package ninja.trek.chiseledenchanting;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChiseledEnchanting implements ModInitializer {
	public static final String MOD_ID = "chiseled-enchanting";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Block CHISELED_ENCHANTING_TABLE = new ChiseledEnchantmentTableBlock(
			FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE)
	);

	public static final BlockEntityType<ChiseledEnchantmentTableBlockEntity> CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY =
			FabricBlockEntityTypeBuilder.create(
					ChiseledEnchantmentTableBlockEntity::new,
					CHISELED_ENCHANTING_TABLE
			).build();

	@Override
	public void onInitialize() {
		Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of(MOD_ID, "chiseled_enchanting_table"),
				CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY
		);
		Registry.register(
				Registries.BLOCK,
				Identifier.of(MOD_ID, "chiseled_enchanting_table"),
				CHISELED_ENCHANTING_TABLE
		);

		Registry.register(
				Registries.ITEM,
				Identifier.of(MOD_ID, "chiseled_enchanting_table"),
				new BlockItem(CHISELED_ENCHANTING_TABLE, new Item.Settings())
		);




		LOGGER.info("Chiseled Enchanting mod initialized!");
	}
}