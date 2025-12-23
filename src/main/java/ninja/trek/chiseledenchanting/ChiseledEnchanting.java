package ninja.trek.chiseledenchanting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChiseledEnchanting implements ModInitializer {
	public static final String MOD_ID = "chiseled-enchanting";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Block CHISELED_ENCHANTING_TABLE;
	public static BlockEntityType<ChiseledEnchantmentTableBlockEntity> CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		// Create identifier
		Identifier id = Identifier.of(MOD_ID, "chiseled_enchanting_table");
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);

		// Create settings - registry key required for loot table lookup during block construction
		AbstractBlock.Settings settings = AbstractBlock.Settings.create()
				.registryKey(blockKey)
				.mapColor(MapColor.RED)
				.strength(5.0F, 1200.0F)
				.sounds(BlockSoundGroup.STONE)
				.requiresTool()
				.pistonBehavior(PistonBehavior.BLOCK);

		// Register block
		CHISELED_ENCHANTING_TABLE = Registry.register(
				Registries.BLOCK,
				id,
				new ChiseledEnchantmentTableBlock(settings)
		);

		// Register block entity type
		CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				id,
				FabricBlockEntityTypeBuilder.create(
						ChiseledEnchantmentTableBlockEntity::new,
						CHISELED_ENCHANTING_TABLE
				).build()
		);

		// Register block item
		Registry.register(
				Registries.ITEM,
				id,
				new BlockItem(
						CHISELED_ENCHANTING_TABLE,
						new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
				)
		);

		LOGGER.info("Chiseled Enchanting mod initialized!");
	}
}
