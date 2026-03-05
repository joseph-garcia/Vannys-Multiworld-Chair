package net.vanny.vannysmultiworldpapasan.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.vanny.vannysmultiworldpapasan.block.entity.ModBlockEntities;
import net.vanny.vannysmultiworldpapasan.block.entity.PapasanBlockEntity;
import org.jetbrains.annotations.Nullable;
import java.util.List;



public class PapasanChairBlock extends HorizontalDirectionalBlock implements EntityBlock {


    public PapasanChairBlock(Properties properties) {
        super(properties);
        // Set the default orientation
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PapasanBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;

        return type == ModBlockEntities.PAPASAN_BE.get() ? (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof PapasanBlockEntity papasanBe) {
                PapasanBlockEntity.tick(level1, pos, state1, papasanBe);
            }
        } : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            long time = level.getDayTime() % 24000;
            boolean isRaining = level.isRaining();

            // specific tick windows
            boolean validClear = !isRaining && (time >= 12542 && time <= 23459);
            boolean validRain = isRaining && (time >= 12010 && time <= 23991);

            if (validClear || validRain) {

                // Clean up any seats with no passengers before trying to sit
                List<AreaEffectCloud> emptySeats = level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(pos))
                        .stream().filter(e -> !e.isVehicle()).toList();
                emptySeats.forEach(Entity::discard);

                if (!level.isClientSide()) {
                    // Check if a seat already exists here
                    List<AreaEffectCloud> existingSeats = level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(pos));

                    if (existingSeats.isEmpty()) {
                        // AreaEffectCloud is invisible and has no hitbox
                        AreaEffectCloud seat = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY() - 0.2, pos.getZ() + 0.5);
                        seat.setDuration(Integer.MAX_VALUE); // Make sure it doesn't disappear
                        seat.setRadius(0f);                  // No potion cloud effect
                        seat.setWaitTime(0);

                        level.addFreshEntity(seat);
                        player.startRiding(seat);

                        return InteractionResult.SUCCESS;
                    }
                }

                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.literal("It's not late enough to nap yet."), true);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());


    }

    // This removes the invisible seat if the block broken
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            List<AreaEffectCloud> seats = level.getEntitiesOfClass(AreaEffectCloud.class, new net.minecraft.world.phys.AABB(pos));
            for (AreaEffectCloud seat : seats) {
                seat.discard();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // determine the direction when the block is placed
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

}