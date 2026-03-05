package net.vanny.vannysmultiworldpapasan.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PapasanBlockEntity extends BlockEntity {
    private int sleepTimer = 0;
    private static final int SLEEP_THRESHOLD = 60; // 3 seconds (20 ticks * 3)

    public PapasanBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PAPASAN_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PapasanBlockEntity blockEntity) {
        if (level.isClientSide) return;

        // Look for the seat entity
        List<AreaEffectCloud> seats = level.getEntitiesOfClass(
                AreaEffectCloud.class,
                new AABB(pos).inflate(0.5)
        );

        if (!seats.isEmpty()) {
            AreaEffectCloud seat = seats.get(0);

            if (!seat.getPassengers().isEmpty() && seat.getPassengers().get(0) instanceof ServerPlayer player) {

                blockEntity.sleepTimer++;

                if (blockEntity.sleepTimer >= SLEEP_THRESHOLD) {
                    blockEntity.performSleep(player, (ServerLevel) level);
                    blockEntity.sleepTimer = 0;
                }

                return; // stop if seated
            }
        }

        // No valid seated player, reset timer
        blockEntity.sleepTimer = 0;
    }

    private void performSleep(ServerPlayer player, ServerLevel level) {
        long currentTime = level.getDayTime() % 24000;
        long timeUntilMorning = 24000 - currentTime;

        if (!(currentTime < 12542)) {
            level.getServer().getCommands().performPrefixedCommand(
                    level.getServer().createCommandSourceStack().withPermission(4),
                    "time add " + timeUntilMorning
            );
        }

        // Clear Weather if raining
        if (level.isRaining() || level.isThundering()) {
            level.setWeatherParameters(6000, 0, false, false);
        }

        // reset phantom timer
        player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));

        // kick player from chair
        player.stopRiding();

        player.displayClientMessage(Component.literal("You feel well rested!"), true);
    }
}