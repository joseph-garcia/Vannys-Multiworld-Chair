package net.vanny.vannysmultiworldpapasan.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class PapasanChairBlock extends HorizontalDirectionalBlock {
    public PapasanChairBlock(Properties properties) {
        super(properties);
        // Set the default orientation
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        // Clean up any "dead" seats (seats with no passengers) before trying to sit
        List<AreaEffectCloud> emptySeats = level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(pos))
                .stream().filter(e -> !e.isVehicle()).toList();
        emptySeats.forEach(Entity::discard);

        // Replace the sitting logic inside your use() method with this:
        if (!level.isClientSide()) {
            // Check if a seat already exists here
            List<AreaEffectCloud> existingSeats = level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(pos));

            if (existingSeats.isEmpty()) {
                // AreaEffectCloud is invisible and has no hitbox by default!
                AreaEffectCloud seat = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                seat.setDuration(Integer.MAX_VALUE); // Make sure it doesn't disappear
                seat.setRadius(0f);                  // No potion cloud effect
                seat.setWaitTime(0);

                level.addFreshEntity(seat);
                player.startRiding(seat);

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    // This removes the invisible "seat" if the block is broken
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            List<AreaEffectCloud> seats = level.getEntitiesOfClass(AreaEffectCloud.class, new net.minecraft.world.phys.AABB(pos));
            for (AreaEffectCloud seat : seats) {
                seat.discard(); // Remove the invisible seat
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    // This tells Minecraft the block has a "Facing" property
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // This determines the direction when the block is placed
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

}