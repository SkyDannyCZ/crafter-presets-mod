# Crafter Presets Mod

A client-side Fabric mod for Minecraft 1.21.1 that adds a **Presets** button to the vanilla Crafter block screen. Save and load slot-disable patterns so you can switch recipes instantly without reconfiguring every time.

---

## What It Does

When you open a Crafter block in-game, a **Presets** button appears next to the GUI. Click it to:

- **Save** your current disabled-slot pattern as a named preset (e.g. "Crafting Table", "Bone Block")
- **Load** any saved preset — it automatically clicks the slots in-game to match
- **Delete** presets you no longer need

Presets are saved to `.minecraft/config/crafter-presets.json` and persist across sessions.

---

## Requirements

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/use/installer/) `0.16.0+`
- [Fabric API](https://modrinth.com/mod/fabric-api) `0.102.0+1.21.1`
- Java **21**

---

## How to Build

### 1. Install Java 21

Download from [adoptium.net](https://adoptium.net/) and install it.

Verify with:
```bash
java -version
```
You should see `openjdk 21`.

### 2. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/crafter-presets-mod.git
cd crafter-presets-mod
```

### 3. Build the mod

On Windows:
```bash
gradlew.bat build
```

On Mac/Linux:
```bash
./gradlew build
```

The first build downloads Minecraft mappings and dependencies — it takes a few minutes. After that it's fast.

### 4. Find the JAR

The built mod file is at:
```
build/libs/crafter-presets-1.0.0.jar
```

---

## How to Install

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.1
3. Put both `fabric-api-*.jar` and `crafter-presets-1.0.0.jar` into your `.minecraft/mods/` folder
4. Launch Minecraft using the Fabric profile

---

## How to Use In-Game

1. Place a **Crafter** block and open it
2. Click the **Presets** button on the right side of the GUI
3. The Preset Manager screen opens:
   - Type a name in the text box and click **Save** to save your current slot pattern
   - Click any preset in the list to select it
   - Click **Load** to apply it — slots will be toggled automatically
   - Click **Delete** to remove the selected preset
4. Click **Back** to return to the Crafter

---

## Project Structure

```
crafter-presets-mod/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── src/main/
    ├── java/com/crafterpresets/
    │   ├── client/
    │   │   ├── CrafterPresetsClient.java   — mod entrypoint
    │   │   ├── PresetStorage.java          — save/load presets to JSON
    │   │   └── PresetListScreen.java       — the preset manager screen
    │   └── mixin/
    │       └── CrafterScreenMixin.java     — injects Presets button into CrafterScreen
    └── resources/
        ├── fabric.mod.json
        ├── crafterpresets.mixins.json
        └── assets/crafterpresets/lang/
            └── en_us.json
```

---

## Common Preset Patterns

| Recipe | Disabled slots |
|---|---|
| Crafting Table | 1, 2, 3, 4, 7 (top row + left col minus 2×2) |
| Stick | 1, 2, 3, 4, 6, 7, 8, 9 (centre column only) |
| Bone Block | none (all 9 used) |
| Dried Kelp Block | none (all 9 used) |

Slot numbering:
```
1 2 3
4 5 6
7 8 9
```

---

## License

MIT
