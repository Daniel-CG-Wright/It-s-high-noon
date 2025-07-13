package com.highnoon.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.highnoon.KeepInvOnPvpDeath;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
    public abstract class PlayerDeathMixin {
       @Final @Shadow PlayerInventory inventory;

    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    public void onDropInventory(CallbackInfo info) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this); // Getting the "this" instance in the PlayerEntity class
            if (KeepInvOnPvpDeath.currentlyDeadByPlayer.get(player.getUuid()) != null) {
                // Not using the player variable here as the @Shadow annotation allows me to use the "this" instance directly
                info.cancel();
            }
            
    }
}