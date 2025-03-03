package com.astrazoey.secondchance.mixins;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.*;


import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;





@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    float playerHealth;
    float damageAmount;
    boolean validBraceSource;
    float damageRemainder = 1.0f;
    float damageThreshold = 13.5f;

    @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
    public void getPlayerHealth(DamageSource source, float amount, CallbackInfo ci) {
        playerHealth = this.getHealth();
        damageAmount = amount;

        if(source.isFromFalling() || source.isOutOfWorld() || source.isFallingBlock()) {
            validBraceSource = false;
        } else {
            validBraceSource = true;
        }
    }

    @ModifyArg(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V"), index = 0)
    private float adjustFinalHealth(float amount) {
        if(amount <= 0.0f && playerHealth >= damageThreshold && validBraceSource) {
            amount = damageRemainder;
        }
        return amount;
    }
}
