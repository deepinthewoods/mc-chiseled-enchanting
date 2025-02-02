package ninja.trek.chiseledenchanting;

import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import ninja.trek.chiseledenchanting.mixin.EnchantmentScreenHandlerAccessor;

public class ChiseledEnchantmentScreenHandler extends EnchantmentScreenHandler {
    public ChiseledEnchantmentScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(syncId, inventory, context);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // Use the mixin accessor to obtain the context, since the field is private
        ScreenHandlerContext context = ((EnchantmentScreenHandlerAccessor)this).getContext();
        return context.get((world, pos) ->
                world.getBlockState(pos).isOf(ChiseledEnchanting.CHISELED_ENCHANTING_TABLE), true);
    }
}
