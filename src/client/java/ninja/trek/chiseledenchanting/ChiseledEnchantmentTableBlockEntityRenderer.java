package ninja.trek.chiseledenchanting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantmentTableBlockEntityRenderer implements BlockEntityRenderer<ChiseledEnchantmentTableBlockEntity, ChiseledEnchantmentTableRenderState> {
    public static final SpriteIdentifier BOOK_TEXTURE;
    private final BookModel book;
    private final SpriteHolder spriteHolder;

    public ChiseledEnchantmentTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.spriteHolder = ctx.spriteHolder();
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public ChiseledEnchantmentTableRenderState createRenderState() {
        return new ChiseledEnchantmentTableRenderState();
    }

    @Override
    public void updateRenderState(ChiseledEnchantmentTableBlockEntity entity, ChiseledEnchantmentTableRenderState state, float tickDelta, Vec3d cameraPos, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickDelta, cameraPos, crumblingOverlay);
        state.pageAngle = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle);
        state.pageTurningSpeed = MathHelper.lerp(tickDelta, entity.pageTurningSpeed, entity.nextPageTurningSpeed);
        state.ticks = entity.ticks + tickDelta;

        float rotationDiff = entity.bookRotation - entity.lastBookRotation;
        while (rotationDiff >= Math.PI) {
            rotationDiff -= (Math.PI * 2F);
        }
        while (rotationDiff < -Math.PI) {
            rotationDiff += (Math.PI * 2F);
        }
        state.bookRotation = entity.lastBookRotation + rotationDiff * tickDelta;
    }

    @Override
    public void render(ChiseledEnchantmentTableRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        matrices.push();
        matrices.translate(0.5F, 0.75F, 0.5F);

        matrices.translate(0.0F, 0.1F + MathHelper.sin(state.ticks * 0.1F) * 0.01F, 0.0F);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-state.bookRotation));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));

        float leftPage = MathHelper.fractionalPart(state.pageAngle + 0.25F) * 1.6F - 0.3F;
        float rightPage = MathHelper.fractionalPart(state.pageAngle + 0.75F) * 1.6F - 0.3F;

        // Create BookModelState with correct parameters (pageTurnAmount, leftFlipAmount, rightFlipAmount, pageTurnSpeed)
        BookModel.BookModelState bookModelState = new BookModel.BookModelState(
            state.ticks,
            MathHelper.clamp(leftPage, 0.0F, 1.0F),
            MathHelper.clamp(rightPage, 0.0F, 1.0F),
            state.pageTurningSpeed
        );

        // Submit the book model to the render queue
        queue.submitModel(
            this.book,
            bookModelState,
            matrices,
            BOOK_TEXTURE.getRenderLayer(RenderLayer::getEntitySolid),
            state.lightmapCoordinates,
            OverlayTexture.DEFAULT_UV,
            0xFFFFFFFF,
            this.spriteHolder.getSprite(BOOK_TEXTURE),
            0,
            state.crumblingOverlay
        );

        matrices.pop();
    }

    static {
        BOOK_TEXTURE = TexturedRenderLayers.ENTITY_SPRITE_MAPPER.mapVanilla("enchanting_table_book");
    }
}
