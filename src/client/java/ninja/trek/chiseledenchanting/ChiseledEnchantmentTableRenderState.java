package ninja.trek.chiseledenchanting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantmentTableRenderState extends BlockEntityRenderState {
    public float ticks;
    public float bookRotation;
    public float pageAngle;
    public float pageTurningSpeed;
}
