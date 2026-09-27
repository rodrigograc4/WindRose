package com.rodrigograc4.windrose.mixin;

import com.rodrigograc4.windrose.config.WindRoseConfig;
import com.rodrigograc4.windrose.util.WorldKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class TotemUseMixin {

    // Injected after vanilla's thread check, so this only runs once, on the client thread
    @Inject(
            method = "handleEntityEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void windrose$onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.getEventId() != EntityEvent.PROTECTED_FROM_DEATH) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;
        if (packet.getEntity(client.level) != client.player) return;

        WindRoseConfig.INSTANCE.incrementTotems(WorldKey.current());
    }
}
