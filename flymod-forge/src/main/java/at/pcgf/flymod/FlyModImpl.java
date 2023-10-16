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

package at.pcgf.flymod;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
// import org.lwjgl.glfw.GLFW;

import static at.pcgf.flymod.FlyingState.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(FlyModImpl.MOD_ID)
public class FlyModImpl {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "flymod";

    public static FlyingState flyingState = NOT_FLYING;
    private static final KeyMapping flyKey = new KeyMapping(
        "key.flymod.toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_B,
        // GLFW.GLFW_KEY_B,
        "key.flymod.keybinding"
    );

    public FlyModImpl() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        modLoadingContext.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::clientTickHandler);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ClientRegistry.registerKeyBinding(flyKey);
    }

    private void clientTickHandler(ClientTickEvent event) {
        if (flyKey.consumeClick()) {
            flyingState = flyingState == FLYING ? NEUTRAL : FLYING;
        }
    }
}