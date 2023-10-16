package at.pcgf.flymod.mixin;

import at.pcgf.flymod.FlyModImpl;
import at.pcgf.flymod.FlyingState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class LivingEntityMixin extends LivingEntity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void move(MoverType type, Vec3 vec3d) {
        if (FlyModImpl.flyingState == FlyingState.FLYING) {
            fallDistance = 0.0F;
        }
        super.move(type, vec3d);
    }
}
