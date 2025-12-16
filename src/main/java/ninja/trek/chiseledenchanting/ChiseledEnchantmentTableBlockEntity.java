package ninja.trek.chiseledenchanting;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serialization;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ChiseledEnchantmentTableBlockEntity extends BlockEntity {
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    private static final Random RANDOM = Random.create();
    private final Map<Enchantment, Integer> enchantmentBoosts = new HashMap<>();
    @Nullable
    private Text customName;

    public ChiseledEnchantmentTableBlockEntity(BlockPos pos, BlockState state) {
        super(ChiseledEnchanting.CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ChiseledEnchantmentTableBlockEntity blockEntity) {
        blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
        blockEntity.lastBookRotation = blockEntity.bookRotation;

        // Track players nearby
        PlayerEntity playerEntity = world.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double dx = playerEntity.getX() - (pos.getX() + 0.5);
            double dz = playerEntity.getZ() - (pos.getZ() + 0.5);
            blockEntity.targetBookRotation = (float) MathHelper.atan2(dz, dx);
            blockEntity.nextPageTurningSpeed += 0.1F;

            if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float oldFlipRandom = blockEntity.flipRandom;
                do {
                    blockEntity.flipRandom += RANDOM.nextInt(4) - RANDOM.nextInt(4);
                } while (oldFlipRandom == blockEntity.flipRandom);
            }
        } else {
            blockEntity.targetBookRotation += 0.02F;
            blockEntity.nextPageTurningSpeed -= 0.1F;
        }

        // Normalize book rotation
        while (blockEntity.bookRotation >= Math.PI) blockEntity.bookRotation -= (Math.PI * 2F);
        while (blockEntity.bookRotation < -Math.PI) blockEntity.bookRotation += (Math.PI * 2F);
        while (blockEntity.targetBookRotation >= Math.PI) blockEntity.targetBookRotation -= (Math.PI * 2F);
        while (blockEntity.targetBookRotation < -Math.PI) blockEntity.targetBookRotation += (Math.PI * 2F);

        // Smooth book rotation
        float rotationDiff = blockEntity.targetBookRotation - blockEntity.bookRotation;
        while (rotationDiff >= Math.PI) rotationDiff -= (Math.PI * 2F);
        while (rotationDiff < -Math.PI) rotationDiff += (Math.PI * 2F);
        blockEntity.bookRotation += rotationDiff * 0.4F;

        // Clamp page turning speed
        blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);

        // Update tick count
        blockEntity.ticks++;
        blockEntity.pageAngle = blockEntity.nextPageAngle;

        // Smooth page turning animation
        float pageFlipDelta = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4F;
        pageFlipDelta = MathHelper.clamp(pageFlipDelta, -0.2F, 0.2F);
        blockEntity.flipTurn += (pageFlipDelta - blockEntity.flipTurn) * 0.9F;
        blockEntity.nextPageAngle += blockEntity.flipTurn;

        // Update enchantment boosts
        blockEntity.updateEnchantmentBoosts(world, pos);
    }

    private void updateEnchantmentBoosts(World world, BlockPos pos) {
        enchantmentBoosts.clear();

        // Scan for chiseled bookshelves (2 blocks away in XZ)
        for (BlockPos scanPos : BlockPos.iterate(pos.add(-2, 0, -2), pos.add(2, 3, 2))) {
            BlockState scanState = world.getBlockState(scanPos);
            if (scanState.getBlock() instanceof net.minecraft.block.ChiseledBookshelfBlock) {
                BlockEntity bookshelfEntity = world.getBlockEntity(scanPos);
                if (bookshelfEntity instanceof Inventory inventory) {
                    processBookshelfInventory(inventory);
                }
            }
        }

        // Scan for armor stands (3 blocks away in XZ)
        Box armorStandBox = new Box(
            pos.getX() - 3, pos.getY(), pos.getZ() - 3,
            pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4
        );
        List<ArmorStandEntity> armorStands = world.getEntitiesByClass(
            ArmorStandEntity.class, armorStandBox, entity -> true
        );

        for (ArmorStandEntity armorStand : armorStands) {
            processArmorStandEquipment(armorStand);
        }
    }

    private void processBookshelfInventory(Inventory inventory) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                var bookEnchants = stack.get(DataComponentTypes.ENCHANTMENTS).getEnchantments(); // List of Enchantment objects
                for (RegistryEntry<Enchantment> enchantment : bookEnchants) {
                    int currentBoost = enchantmentBoosts.getOrDefault(enchantment, 0);
                    enchantmentBoosts.put(enchantment.value(), Math.min(3, currentBoost + 1)); // Max boost of 3 books
                }
            }
        }
    }

    private void processArmorStandEquipment(ArmorStandEntity armorStand) {
        // Check all equipment slots: helmet, chestplate, leggings, boots, mainhand, offhand
        for (ItemStack stack : armorStand.getArmorItems()) {
            processEquipmentItem(stack);
        }
        for (ItemStack stack : armorStand.getHandItems()) {
            processEquipmentItem(stack);
        }
    }

    private void processEquipmentItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        // Get enchantments from the item
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments != null && !enchantments.isEmpty()) {
            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                int currentBoost = enchantmentBoosts.getOrDefault(enchantment.value(), 0);
                enchantmentBoosts.put(enchantment.value(), Math.min(3, currentBoost + 1)); // Max boost of 3 total
            }
        }
    }




    public float getEnchantmentBoost(Enchantment enchantment) {
        int bookCount = enchantmentBoosts.getOrDefault(enchantment, 0);
        float boost = (float) Math.pow(2, bookCount);
        return boost;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registryLookup));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("CustomName", 8)) {
            this.customName = Text.Serialization.fromJson(nbt.getString("CustomName"), registryLookup);
        }
    }

    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable Text customName) {
        this.customName = customName;
    }
}
