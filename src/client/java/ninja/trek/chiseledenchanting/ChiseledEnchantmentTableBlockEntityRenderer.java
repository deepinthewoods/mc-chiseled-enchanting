package ninja.trek.chiseledenchanting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import ninja.trek.chiseledenchanting.ChiseledEnchantmentTableBlockEntity;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantmentTableBlockEntityRenderer implements BlockEntityRenderer<ChiseledEnchantmentTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEXTURE;
    private final BookModel book;

    public ChiseledEnchantmentTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(ChiseledEnchantmentTableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0.75F, 0.5F);

        float time = (entity.getWorld() != null) ? (entity.getWorld().getTime() + tickDelta) : 0;
        matrices.translate(0.0F, 0.1F + MathHelper.sin(time * 0.1F) * 0.01F, 0.0F);

        float rotationDiff;
        for (rotationDiff = entity.bookRotation - entity.lastBookRotation; rotationDiff >= Math.PI; rotationDiff -= (Math.PI * 2F)) {
        }

        while (rotationDiff < -Math.PI) {
            rotationDiff += (Math.PI * 2F);
        }

        float bookRotation = entity.lastBookRotation + rotationDiff * tickDelta;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-bookRotation));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));

        float pageAngle = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle);
        float leftPage = MathHelper.fractionalPart(pageAngle + 0.25F) * 1.6F - 0.3F;
        float rightPage = MathHelper.fractionalPart(pageAngle + 0.75F) * 1.6F - 0.3F;
        float pageTurningSpeed = MathHelper.lerp(tickDelta, entity.pageTurningSpeed, entity.nextPageTurningSpeed);

        this.book.setPageAngles(time, MathHelper.clamp(leftPage, 0.0F, 1.0F), MathHelper.clamp(rightPage, 0.0F, 1.0F), pageTurningSpeed);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        this.book.renderBook(matrices, vertexConsumer, light, overlay, -1);

        matrices.pop();
    }

    static {
        BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of("entity/enchanting_table_book"));
    }
}
