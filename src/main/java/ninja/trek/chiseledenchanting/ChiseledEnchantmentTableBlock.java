package ninja.trek.chiseledenchanting;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.AbstractBlock;

public class ChiseledEnchantmentTableBlock extends BlockWithEntity {

    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    private static final VoxelShape FLOATING_BOOK_SHAPE = Block.createCuboidShape(4.0, 12.0, 4.0, 12.0, 14.0, 12.0);
    private static final VoxelShape SHAPE = VoxelShapes.union(BASE_SHAPE, FLOATING_BOOK_SHAPE);

    public ChiseledEnchantmentTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<ChiseledEnchantmentTableBlock> getCodec() {
        return MapCodec.of(Encoder.empty(), Decoder.unit(this));
    }

    @Nullable
    protected static <E extends BlockEntity, T extends BlockEntity> BlockEntityTicker<T> checkType(World world,
                                                                                                   BlockEntityType<T> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<T>) ticker : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? checkType(world, type, ChiseledEnchanting.CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY,
                ChiseledEnchantmentTableBlockEntity::tick) : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChiseledEnchantmentTableBlockEntity(pos, state);
    }



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChiseledEnchantmentTableBlockEntity) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }



    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                new ChiseledEnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
                Text.translatable("container.enchant")
        );
    }



    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChiseledEnchantmentTableBlockEntity) {
                // Handle any cleanup if needed
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}