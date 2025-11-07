package ninja.trek.chiseledenchanting.mixin;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.EnchantmentScreenHandler;
import ninja.trek.chiseledenchanting.ChiseledEnchanting;
import ninja.trek.chiseledenchanting.ChiseledEnchantingConfig;
import ninja.trek.chiseledenchanting.ChiseledEnchantmentTableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    private final Random random = new Random();


    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private void modifyEnchantmentProbabilities(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (!ChiseledEnchantingConfig.get().enableWeighting) {
            return;
        }

        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;
        ((EnchantmentScreenHandlerAccessor) handler).getContext().run((world, pos) -> {
            if (world.getBlockEntity(pos) instanceof ChiseledEnchantmentTableBlockEntity blockEntity) {
                List<EnchantmentLevelEntry> entries = cir.getReturnValue();
                if (entries == null || entries.isEmpty()) return;

                List<EnchantmentLevelEntry> modifiedEntries = new ArrayList<>();
                boolean isBook = stack.isOf(Items.BOOK);
                boolean invertItems = ChiseledEnchantingConfig.get().inverseWeightingForItems; // Global inversion setting

                for (EnchantmentLevelEntry entry : entries) {
                    float boost = blockEntity.getEnchantmentBoost(entry.enchantment().value());

                    // Apply book-specific inversion
                    if (isBook && ChiseledEnchantingConfig.get().inverseWeightingForBooks) {
                        boost = 1.0f / boost;
                    }

                    // Apply general item inversion (but not to books)
                    if (!isBook && invertItems) {
                        boost = 1.0f / boost;
                    }

                    // Ensure at least one copy exists
                    int copies = Math.max(1, Math.round(boost));
                    for (int i = 0; i < copies; i++) {
                        modifiedEntries.add(entry);
                    }
                }

                if (!modifiedEntries.isEmpty()) {
                    List<EnchantmentLevelEntry> finalEntries = new ArrayList<>();
                    finalEntries.add(modifiedEntries.get(random.nextInt(modifiedEntries.size())));
                    cir.setReturnValue(finalEntries);
                }
            }
        });
    }


}