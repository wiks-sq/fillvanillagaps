# Testing Fill Vanilla Gaps

## Setup

Gradle needs a Java 25 JDK. If you don't have one installed, the Minecraft launcher ships one; point Gradle at it in each terminal:

```bash
export JAVA_HOME=~/.minecraft/runtime/java-runtime-epsilon/linux/java-runtime-epsilon
```

## Automated game tests

Twelve server-side game tests cover sorting, harvest, tree felling and leaf decay, building safety, vein mining, the anvil cap, double doors, death drops, Feather Falling, the terracotta blocks, recipes and both data packs. They start a headless Minecraft server, which requires accepting the [Minecraft EULA](https://aka.ms/MinecraftEULA), so they only run when you opt in:

```bash
./gradlew runGameTest -PacceptMinecraftEula=true
```

The run should end with `All 12 required tests passed :)`. A plain `./gradlew build` skips them.

## Dev client

```bash
./gradlew runClient
```

This opens Minecraft 26.3 with the mod and Fabric API, using an offline dev account. Client-only features (the sort key and buttons, the saturation HUD, tooltips) can only be checked this way or in a real install.

## Manual checklist

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

