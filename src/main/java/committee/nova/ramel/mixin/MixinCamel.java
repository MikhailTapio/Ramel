package committee.nova.ramel.mixin;

import committee.nova.ramel.Ramel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(Camel.class)
public abstract class MixinCamel extends LivingEntity {
    @Shadow
    public abstract boolean isDashing();

    @Shadow
    @Nullable
    public abstract LivingEntity getControllingPassenger();

    protected MixinCamel(EntityType<? extends LivingEntity> t, Level w) {
        super(t, w);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void inject$tick(CallbackInfo ci) {
        if (!isDashing() || level().isClientSide) return;
        final int i = hasEffect(MobEffects.MOVEMENT_SPEED) ? Objects.requireNonNull(getEffect(MobEffects.MOVEMENT_SPEED)).getAmplifier() + 1 : 0;
        final int j = hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? Objects.requireNonNull(getEffect(MobEffects.MOVEMENT_SLOWDOWN)).getAmplifier() + 1 : 0;
        final double impactBySpeed = Mth.clamp(getSpeed() * 1.65, .2, 3.0) + .25 * (i - j);
        level().getEntities(this, getBoundingBox().inflate(Ramel.ramImpactRange.get() * (isBaby() ? 1.0 : 2.0)), Entity::isAlive).stream()
                .filter(e -> e instanceof LivingEntity && !getPassengers().contains(e))
                .forEach(e -> {
                    final LivingEntity l = (LivingEntity) e;
                    e.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
                    e.hurt(damageSources().mobAttack(getControllingPassenger() != null ? getControllingPassenger() : this), Ramel.ramDamage.get().floatValue() * (isBaby() ? 1.0F : 2.0F));
                    final double blockedImpact = l.isDamageSourceBlocked(damageSources().mobAttack(this)) ? .5 : 1.0;
                    l.knockback(blockedImpact * impactBySpeed * Ramel.ramKnockBackMultiplier.get() * (isBaby() ? 1.0 : 2.5),
                            Mth.sin(getYRot() * ((float) Math.PI / 180)), -Mth.cos(getYRot() * ((float) Math.PI / 180)));
                });
    }
}
