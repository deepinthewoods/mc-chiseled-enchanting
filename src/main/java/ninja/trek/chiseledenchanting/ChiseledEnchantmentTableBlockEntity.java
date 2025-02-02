package ninja.trek.chiseledenchanting;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.Map;

public class ChiseledEnchantmentTableBlockEntity extends BlockEntity {
    private final Random random = Random.create();
    private final Map<Enchantment, Integer> enchantmentBoosts = new HashMap<>();

    public ChiseledEnchantmentTableBlockEntity(BlockPos pos, BlockState state) {
        super(ChiseledEnchanting.CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ChiseledEnchantmentTableBlockEntity blockEntity) {
        blockEntity.updateEnchantmentBoosts(world, pos);
    }

    private void updateEnchantmentBoosts(World world, BlockPos pos) {
        enchantmentBoosts.clear();

        // Scan in a 5x4x5 area (one block higher than vanilla)
        for (BlockPos scanPos : BlockPos.iterate(pos.add(-2, 0, -2), pos.add(2, 3, 2))) {
            BlockState scanState = world.getBlockState(scanPos);
            if (scanState.getBlock() instanceof ChiseledBookshelfBlock) {
                BlockEntity bookshelfEntity = world.getBlockEntity(scanPos);
                if (bookshelfEntity instanceof Inventory inventory) {
                    processBookshelfInventory(inventory);
                }
            }
        }
    }

    private void processBookshelfInventory(Inventory inventory) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                var bookEnchants = EnchantmentHelper.getEnchantments(stack);

                for (var entry : bookEnchants.getEnchantmentEntries()) {
                    Enchantment enchantment = entry.getKey().value();
                    int currentBoost = enchantmentBoosts.getOrDefault(enchantment, 0);
                    // Cap at 3 books (8x boost)
                    enchantmentBoosts.put(enchantment, Math.min(3, currentBoost + 1));
                }
            }
        }
    }

    public float getEnchantmentBoost(Enchantment enchantment) {
        int bookCount = enchantmentBoosts.getOrDefault(enchantment, 0);
        float boost = (float) Math.pow(2, bookCount); // Normal boost calculation

        // Apply global item inversion if enabled
        if (ChiseledEnchantingConfig.get().inverseWeightingForItems) {
            boost = 1.0f / boost;
        }

        return boost;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList boostList = new NbtList();
        for (Map.Entry<Enchantment, Integer> entry : enchantmentBoosts.entrySet()) {
            NbtCompound enchantNbt = new NbtCompound();
            enchantNbt.putString("id", RegistryKeys.ENCHANTMENT.getValue().toString());
            enchantNbt.putInt("boost", entry.getValue());
            boostList.add(enchantNbt);
        }
        nbt.put("EnchantmentBoosts", boostList);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        enchantmentBoosts.clear();
        NbtList boostList = nbt.getList("EnchantmentBoosts", 10);
        for (int i = 0; i < boostList.size(); i++) {
            NbtCompound enchantNbt = boostList.getCompound(i);
            try {
                Identifier id = Identifier.of(enchantNbt.getString("id"));
                Enchantment enchantment = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(RegistryKey.of(RegistryKeys.ENCHANTMENT, id)).value();
                if (enchantment != null) {
                    enchantmentBoosts.put(enchantment, enchantNbt.getInt("boost"));
                }
            } catch (Exception e) {
                ChiseledEnchanting.LOGGER.error("Failed to load enchantment: ", e);
            }
        }
    }
}