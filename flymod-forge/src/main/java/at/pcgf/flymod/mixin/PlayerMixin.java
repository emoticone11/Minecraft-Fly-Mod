/*
 * Copyright (C) 2017 MarkusWME RatzzFatzz
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgf.flymod.mixin;

import at.pcgf.flymod.gui.FlyModConfigManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector4f;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static at.pcgf.flymod.FlyModImpl.flyingState;
import static at.pcgf.flymod.FlyingState.*;

@SuppressWarnings("unused")
@Mixin(AbstractClientPlayer.class)
public abstract class PlayerMixin extends Player {

    protected PlayerMixin(Level world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Override
    public void move(MoverType type, Vec3 vec3d) {
        Minecraft client = Minecraft.getInstance();
        toggleFlying();

        if (getAbilities().flying && isActiveForCurrentGamemode()) {
            boolean backwards = client.options.keyDown.isDown();
            boolean forwards = client.options.keyUp.isDown();
            boolean left = client.options.keyLeft.isDown();
            boolean right = client.options.keyRight.isDown();

            Vec3 vec = mouseControlMovement(vec3d, backwards, forwards, left, right);
            fadeMovement(backwards || forwards || left || right);
            vec = verticalMovement(vec);
            vec = applyFlyMultiplier(vec);

            setShiftKeyDown(false);
            setSprinting(false);
            super.move(type, vec);

        } else if (!getAbilities().flying && isActiveForCurrentGamemode()) {
            Vec3 vec = vec3d;
            if (client.options.keySprint.isDown() || client.options.toggleSprint) {
                if (!FlyModConfigManager.getConfig().overrideExhaustion) {
                    setSprinting(true);
                    // -0.3 to account for the boost by setSprinting(true)
                    vec = applyRunMultiplier(vec, FlyModConfigManager.getConfig().runSpeedMultiplier - 0.3F);
                } else {
                    setSprinting(false);
                    vec = applyRunMultiplier(vec, FlyModConfigManager.getConfig().runSpeedMultiplier);
                }
                setSwimming(isUnderWater());
            }
            super.move(type, vec);
        } else {
            super.move(type, vec3d);
        }
    }

    private void toggleFlying() {
        if (!isActiveForCurrentGamemode()) {
            return;
        }

        if (flyingState == FLYING) {
            getAbilities().flying = true;
        } else if (flyingState == NEUTRAL) {
            flyingState = NOT_FLYING;
            getAbilities().flying = false;
        } else if (flyingState == NOT_FLYING && getAbilities().flying) {
            flyingState = FLYING;
        }

        onUpdateAbilities();
    }

    private boolean isActiveForCurrentGamemode() {
        return !(FlyModConfigManager.getConfig().onlyForCreative && !getAbilities().instabuild);
    }

    private Vec3 mouseControlMovement(Vec3 vec3d, boolean backwards, boolean forwards, boolean left, boolean right) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            float pitch = yRotO;
            float yaw = xRotO;
            Vector4f directionsVector = new Vector4f(
                    (backwards ? 1 : 0) - (forwards ? 1 : 0),
                    0,
                    (left ? 1 : 0) - (right ? 1 : 0),
                    1);
            directionsVector.normalize();
            float length = (float) Math.sqrt((vec3d.x() * vec3d.x()) + (vec3d.z() * vec3d.z()));
            Vector4f movementVector = multiply4dVector(directionsVector, length);
            // roll yaw pitch degree
            movementVector.transform(new Quaternion(0, -(yaw - 90), pitch, true));

            float resultX = movementVector.x() / movementVector.w();
            float resultY = movementVector.y() / movementVector.w();
            float resultZ = movementVector.z() / movementVector.w();
            return new Vec3(
                    Double.isNaN(resultX) ? 0 : resultX,
                    Double.isNaN(resultY) ? 0 : resultY,
                    Double.isNaN(resultZ) ? 0 : resultZ
            );
        }
        return vec3d;
    }

    private Vector4f multiply4dVector(Vector4f vector, float length) {
        return new Vector4f(
                vector.x() * length,
                vector.y() * length,
                vector.z() * length,
                vector.w() * length
        );
    }

    private void fadeMovement(boolean isMoving) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            return;
        }
        if (!isMoving && !FlyModConfigManager.getConfig().fadeMovement) {
            setDeltaMovement(0.0, 0.0, 0.0);
        }
    }

    private Vec3 verticalMovement(Vec3 vec3d) {
        double y = vec3d.y();
        double flyUpDownBlocks = FlyModConfigManager.getConfig().flyUpDownBlocks;
        if (Minecraft.getInstance().options.keyShift.isDown()) {
            y -= flyUpDownBlocks;
        } else if (Minecraft.getInstance().options.keyJump.isDown()) {
            y += flyUpDownBlocks;
        }
        return new Vec3(vec3d.x(), y, vec3d.z());
    }

    private Vec3 applyFlyMultiplier(double x, double y, double z) {
        boolean speedEnabled = Minecraft.getInstance().options.keySprint.isDown();
        float multiplier = speedEnabled ? FlyModConfigManager.getConfig().flySpeedMultiplier : 1.0f;
        float upDownMultiplier = FlyModConfigManager.getConfig().multiplyUpDown && speedEnabled ? multiplier : 1.0f;
        x *= multiplier;
        y *= upDownMultiplier;
        z *= multiplier;
        return new Vec3(x, y, z);
    }

    private Vec3 applyFlyMultiplier(Vec3 vec3d) {
        return applyFlyMultiplier(vec3d.x(), vec3d.y(), vec3d.z());
    }

    private Vec3 applyRunMultiplier(Vec3 vec, float multiplier) {
        return new Vec3(vec.x() * multiplier, vec.y(), vec.z() * multiplier);
    }

    private boolean isDefaultExhaustion() {
        return !FlyModConfigManager.getConfig().overrideExhaustion
                && Minecraft.getInstance().options.keySprint.isDown()
                || Minecraft.getInstance().options.toggleSprint;
    }
}