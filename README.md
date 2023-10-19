# Minecraft - Fly-Mod
This is a modification of the original [fly mod 3d](https://www.curseforge.com/minecraft/mc-mods/fly-mod-3d) by ratzzfatzz to support Forge 1.18.2.

This Mod for Minecraft allows you to enable a 3D fly and let you customize the fly and run speed.

## Requirements
* Minecraft version 1.18.2
* [Forge 1.18.2 - 40.2.10](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html)
* [Cloth Config (Forge 1.18.2)](https://www.curseforge.com/minecraft/mc-mods/cloth-config/files/4633387)
* Java 17

## How to use

Press the "B" key to toggle fly mode (configurable in Controls). By default, if you hold the forward key, you will fly towards wherever your mouse is pointing. This can be disabled to instead use normal flying controls by setting `mouse_control` to false (see below). Use the sprint key to boost fly speed.

The following configuration options are available in `config/flymod.json` in the minecraft instance files:

```json
{
  "mouse_control": true,
  "only_for_creative": false,
  "fly_up_down_blocks": 0.4,
  "fly_speed_multiplier": 3.0,
  "run_speed_multiplier": 1.5,
  "multiply_up_down": true,
  "fade_movement": false,
  "override_exhaustion": true
}
```

## Supported languages
* Deutsch
* English
