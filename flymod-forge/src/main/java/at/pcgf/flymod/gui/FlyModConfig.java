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

package at.pcgf.flymod.gui;

import at.pcgf.flymod.FlyModImpl;
import com.google.gson.annotations.Expose;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

import java.io.ObjectInputFilter;

public class FlyModConfig {

    @Expose
    public boolean mouseControl = true;

    @Expose
    public boolean onlyForCreative = false;

    @Expose
    public float flyUpDownBlocks = 0.4f;

    @Expose
    public float flySpeedMultiplier = 3;

    @Expose
    public float runSpeedMultiplier = 1.5F;

    @Expose
    public boolean multiplyUpDown = true;

    @Expose
    public boolean fadeMovement = false;

    @Expose
    public boolean overrideExhaustion = true;

    static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(ConfigTexts.TITLE);
        FlyModConfig config = FlyModConfigManager.getConfig();
        builder.getOrCreateCategory(ConfigTexts.CATEGORY)
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.MOUSE_CONTROL, config.mouseControl).setDefaultValue(true).setSaveConsumer(b -> config.mouseControl = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(ConfigTexts.FLY_UP_DOWN_BLOCKS, config.flyUpDownBlocks).setDefaultValue(0.4f).setSaveConsumer(b -> config.flyUpDownBlocks = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(ConfigTexts.FLY_SPEED_MULTIPLIER, config.flySpeedMultiplier).setDefaultValue(2f).setSaveConsumer(b -> config.flySpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(ConfigTexts.RUN_SPEED_MULTIPLIER, config.runSpeedMultiplier).setDefaultValue(1.3f).setSaveConsumer(b -> config.runSpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.MULTIPLY_UP_DOWN1, config.multiplyUpDown).setDefaultValue(true).setSaveConsumer(b -> config.multiplyUpDown = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.FADE_MOVEMENT, config.fadeMovement).setDefaultValue(false).setSaveConsumer(b -> config.fadeMovement = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.CREATIVE_ONLY, config.onlyForCreative).setDefaultValue(false).setSaveConsumer(b -> config.onlyForCreative = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.OVERRIDE_EXHAUSTION, config.overrideExhaustion).setDefaultValue(true).setSaveConsumer(b -> config.overrideExhaustion = b).build());
        builder.setSavingRunnable((FlyModConfigManager::save));
        return builder.build();
    }

    private static class ConfigTexts {
        static final TranslatableComponent TITLE = createTranslatableComponent("text.%s.title");
        static final TranslatableComponent CATEGORY = createTranslatableComponent("text.%s.category.default");
        static final TranslatableComponent MOUSE_CONTROL = createTranslatableComponent("text.%s.option.mouseControl");
        static final TranslatableComponent CREATIVE_ONLY = createTranslatableComponent("text.%s.option.creativeOnly");
        static final TranslatableComponent FLY_UP_DOWN_BLOCKS = createTranslatableComponent("text.%s.option.flyUpDownBlocks");
        static final TranslatableComponent FLY_SPEED_MULTIPLIER = createTranslatableComponent("text.%s.option.flySpeedMultiplier");
        static final TranslatableComponent RUN_SPEED_MULTIPLIER = createTranslatableComponent("text.%s.option.runSpeedMultiplier");
        static final TranslatableComponent MULTIPLY_UP_DOWN1 = createTranslatableComponent("text.%s.option.multiplyUpDown");
        static final TranslatableComponent FADE_MOVEMENT = createTranslatableComponent("text.%s.option.fadeMovement");
        static final TranslatableComponent OVERRIDE_EXHAUSTION = createTranslatableComponent("text.%s.option.overrideExhaustion");

        private static TranslatableComponent createTranslatableComponent(String translationReference) {
            return new TranslatableComponent(String.format(translationReference, FlyModImpl.MOD_ID));
        }
    }

}