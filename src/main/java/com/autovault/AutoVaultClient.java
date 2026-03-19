package com.autovault;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AutoVaultClient implements ClientModInitializer {

    private int tickDelay = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            tickDelay++;
            if (tickDelay < 10) return;
            tickDelay = 0;

            checkNearbyVaults(client);
        });
    }

    private void checkNearbyVaults(MinecraftClient client) {
        ClientPlayerEntity player = client.player;

        Box searchBox = player.getBoundingBox().expand(5);

        int minX = (int) Math.floor(searchBox.minX);
        int minY = (int) Math.floor(searchBox.minY);
        int minZ = (int) Math.floor(searchBox.minZ);
        int maxX = (int) Math.ceil(searchBox.maxX);
        int maxY = (int) Math.ceil(searchBox.maxY);
        int maxZ = (int) Math.ceil(searchBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockEntity be = client.world.getBlockEntity(pos);

                    if (!(be instanceof VaultBlockEntity vault)) continue;

                    var serverData = vault.getServerData();
                    if (serverData == null) continue;

                    var displayItem = serverData.getDisplayItem();
                    if (displayItem == null || displayItem.isEmpty()) continue;

                    if (displayItem.isOf(Items.HEAVY_CORE)) {
                        rightClickVault(client, pos);
                        player.sendMessage(
                            net.minecraft.text.Text.literal(
                                "§b✦ Heavy Core found! Auto clicking vault!"
                            ), true
                        );
                        return;
                    }
                }
            }
        }
    }

    private void rightClickVault(MinecraftClient client, BlockPos pos) {
        ClientPlayerEntity player = client.player;
        Vec3d hitVec = Vec3d.ofCenter(pos);

        BlockHitResult hitResult = new BlockHitResult(
            hitVec,
            net.minecraft.util.math.Direction.UP,
            pos,
            false
        );

        client.interactionManager.interactBlock(
            player,
            Hand.MAIN_HAND,
            hitResult
        );
    }
}
