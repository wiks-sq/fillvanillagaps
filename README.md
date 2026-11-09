# Fill Vanilla Gaps

A Fabric mod for **Minecraft Java 26.3** that adds the small, long-requested features vanilla still lacks. They were chosen by scoring what players vote for and what they actually install; see [RESEARCH.md](RESEARCH.md) for the data and scores.

| Feature | How it works |
|---|---|
| **Inventory & chest sorting** | In any chest, barrel, ender chest, shulker box or your inventory, press **R** or click the small **⇅** button. It sorts the section under your mouse and merges partial stacks. The hotbar is never moved. |
| **Saturation HUD** | A gold outline on the hunger bar shows your hidden saturation. Hold food while hungry and the icons it would refill pulse white. Food tooltips show hunger and saturation numbers. |
| **Right-click harvest** | Right-click a fully grown wheat, carrot, potato, beetroot, nether wart or cocoa plant to harvest it. One seed from the drops is replanted. Crouch or hold bone meal for normal behavior. |
| **Tree felling** | **Crouch** and break a log with an axe to fell the whole tree. It only works on natural trees (logs touching non-player-placed leaves), so log builds are safe. |
| **Vein mining** | **Crouch** and mine an ore with a pickaxe that can harvest it to mine the whole connected vein (up to 64). |
| **Fast leaf decay** | Leaves cut off from a tree disappear within about half a second, and still drop saplings and apples. |
| **No "Too Expensive!"** | Anvil costs are capped at 39 levels instead of being refused. |
| **Double doors** | Opening one door of a pair opens both. Crouch to open just one. |
| **Safe death drops** | Items you drop on death never despawn, survive lava, fire, cactus and explosions, and glow through walls. |
| **Feather Falling** | Boots with Feather Falling no longer trample farmland. |
| **Infinity + Mending** | A bow can have both, as on Bedrock. This is a built-in data pack, on by default. |
| **Rotten flesh → leather** | Smoke it (10 s) or smelt it (20 s). This is a built-in data pack, on by default. |
| **Terracotta slabs & stairs** | 34 new blocks for plain terracotta and all 16 colors, matching 26.3's concrete and wool slabs and stairs. Craft them or use a stonecutter. They appear after terracotta in the Colored Blocks creative tab. |

The extra blocks and vein-mined blocks cost tool durability and hunger like normal mining. Tree felling and vein mining stop before your tool would break.

**Config:** every feature can be switched off in `config/fillvanillagaps.json`, which is created on first launch. The two data packs can be toggled per world with `/datapack list` and `/datapack disable <id>`.

**Multiplayer:** install the mod on both the server and every client, because it adds blocks.

---

## Testing

This machine has no system Java, but the Minecraft launcher ships a full Java 25 JDK. Point Gradle at it in every terminal you build from:

```bash
export JAVA_HOME=~/.minecraft/runtime/java-runtime-epsilon/linux/java-runtime-epsilon
```

### 1. Automated game tests (server-side features)

Twelve game tests cover sorting, harvest, tree felling plus leaf decay, building safety, vein mining, the anvil cap, double doors, death drops, Feather Falling, the terracotta blocks, recipes and both data packs. They start a headless Minecraft server, which means accepting the [Minecraft EULA](https://aka.ms/MinecraftEULA), so you have to opt in:

```bash
./gradlew runGameTest -PacceptMinecraftEula=true
```

The run should end with `All 12 required tests passed :)`. A plain `./gradlew build` skips these tests and only builds the jar.

### 2. Play it in a dev client (fastest)

```bash
./gradlew runClient
```

This opens Minecraft 26.3 with the mod and Fabric API loaded, using an offline dev account. The client features (sort key and button, saturation HUD, tooltips) can only be checked this way or in a real install.

### 3. Install into your normal launcher

1. Run the Fabric installer (https://fabricmc.net/use/installer/) and pick Minecraft 26.3 with loader 0.20.1. This adds a `fabric-loader-0.20.1-26.3` launcher profile.
2. Download Fabric API **0.165.2+26.3** from https://modrinth.com/mod/fabric-api.
3. Build the mod:

   ```bash
   ./gradlew build
   ```

4. Copy `build/libs/fillvanillagaps-1.0.0.jar` and the Fabric API jar into `~/.minecraft/mods/`. Create that folder if it doesn't exist.
5. Start the Fabric profile from the launcher.

### Manual checklist

Create a new world with cheats on, then use `/gamemode survival` for the gameplay checks.

| Check | Steps | Expected |
|---|---|---|
| Sorting | Fill a chest with mixed items in random slots, then hover the chest half and press **R**. Hover your inventory and press **R** again. | The chest is ordered and stacks are merged. Your main inventory is sorted and the hotbar stays put. The **⇅** buttons do the same thing. |
| Saturation HUD | Eat something like a cooked steak. Then run `/effect give @s hunger 10 100` and hold bread. | A gold outline appears around the hunger icons. While hungry and holding bread, the icons it would refill pulse. The bread tooltip reads "Restores 5 hunger, 6.0 saturation". |
| Harvest | Run `/setblock ~2 ~-1 ~ farmland` and then `/setblock ~2 ~ ~ wheat[age=7]`, then right-click the wheat. | Wheat and seeds drop, and the crop is replanted at age 0. |
| Tree felling | Run `/give @s iron_axe`, find a natural tree, **crouch** and break the bottom log. | The whole tree falls and the leaves vanish within a second. Breaking without crouching works normally. |
| Vein mining | Run `/give @s iron_pickaxe`, find an ore vein (or `/fill ~1 ~ ~1 ~2 ~1 ~2 iron_ore`), **crouch** and mine one ore. | The whole connected vein breaks. A wooden pickaxe on iron does nothing extra. |
| Anvil | Run `/give @s diamond_sword[repair_cost=63]`, `/give @s enchanted_book[stored_enchantments={"minecraft:sharpness":5}]` and `/xp add @s 50 levels`, then combine them in an anvil. | The cost shows 39 instead of "Too Expensive!", and you can take the result. |
| Double doors | Place two oak doors side by side, then right-click one. Crouch and right-click. | Both doors open together. Crouching toggles only the one you click. |
| Death drops | Fill your inventory, stand on lava-adjacent ground, and run `/kill @s`. | The drops glow through walls, survive in lava, and are still there after `/tick sprint 7000`, longer than vanilla's 5-minute despawn. |
| Feather Falling | Run `/give @s leather_boots[enchantments={"minecraft:feather_falling":1}]`, wear them, and jump onto farmland from 3 blocks up. | The farmland stays. Without the boots, it turns to dirt. |
| Infinity + Mending | Run `/give @s bow[enchantments={"minecraft:infinity":1}]`, hold it, and run `/enchant @s mending`. | It succeeds. Without the mod, the command says the enchantments are incompatible. |
| Rotten flesh | Put rotten flesh into a smoker or furnace. | You get leather. |
| Terracotta | Open the creative Colored Blocks tab. Craft 3 terracotta in a row, then use a stonecutter. | 34 slabs and stairs appear after the dyed terracotta. Crafting gives 6 slabs, and the stonecutter offers slabs (2) and stairs (1). |

## Project layout

- `src/main/java`: shared and server code (features, mixins, block registration, network payload)
- `src/client/java`: the sort key and buttons, saturation HUD, and tooltips
- `src/gametest/java`: automated game tests
- `src/main/resources/resourcepacks/`: the two built-in data packs
- `tools/gen_terracotta.py`: regenerates all terracotta slab/stair JSON (models, blockstates, loot, recipes, tags, lang) from the vanilla jar

Built with Fabric Loader 0.20.1, Fabric API 0.165.2+26.3, Loom 1.19 and Gradle 9.8.2 on Java 25. Since 26.1, Minecraft ships unobfuscated, so the code uses Mojang's own names.
