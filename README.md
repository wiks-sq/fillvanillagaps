<div align="center">

<img src="src/main/resources/assets/fillvanillagaps/icon.png" alt="Fill Vanilla Gaps icon" width="96">

# Fill Vanilla Gaps

**The small, long-requested features vanilla Minecraft still lacks.**

[![Build](https://github.com/wiks-sq/fillvanillagaps/actions/workflows/build.yml/badge.svg)](https://github.com/wiks-sq/fillvanillagaps/actions/workflows/build.yml)
![Minecraft 26.3](https://img.shields.io/badge/Minecraft-26.3-62B47A)
![Fabric](https://img.shields.io/badge/loader-Fabric-DBD0B4)
![Java 25](https://img.shields.io/badge/Java-25-E76F00)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

</div>

Fill Vanilla Gaps adds thirteen everyday conveniences that players keep asking Mojang for. It's a Fabric mod for Minecraft Java 26.3. Each feature was chosen by comparing what players **vote for** on the official feedback site with the mods they **actually keep installed**. The criteria were: used every session, fits the vanilla feel, and still missing in 26.3. The data and scoring are in [RESEARCH.md](RESEARCH.md).

## Features

| | Feature | How it works |
|---|---|---|
| 🧰 | **Inventory & chest sorting** | Press **R** (rebindable) or click **⇅** in a chest, barrel, ender chest, shulker box or your inventory. It merges partial stacks and orders items, and never touches the hotbar. |
| 🍗 | **Saturation HUD** | A gold outline on the hunger bar shows hidden saturation. Holding food while hungry previews what it refills, and food tooltips show the numbers. |
| 🌾 | **Right-click harvest** | Right-click a ripe crop to harvest and replant it. Works on wheat, carrots, potatoes, beetroot, nether wart and cocoa. |
| 🪓 | **Tree felling** | Crouch and break a log with an axe to fell the whole tree. Natural trees only, so log builds are safe. |
| ⛏️ | **Vein mining** | Crouch and mine an ore with a capable pickaxe to break the whole vein (up to 64). |
| 🍃 | **Fast leaf decay** | Orphaned leaves vanish in about half a second and still drop saplings and apples. |
| ⚒️ | **No "Too Expensive!"** | Anvil jobs are capped at 39 levels. |
| 🚪 | **Double doors** | Open one door and its pair opens too. Crouch to open just one. |
| 💀 | **Safe death drops** | Death drops never despawn, survive lava, fire and explosions, and glow through walls. |
| 🥾 | **Feather Falling** | Boots with Feather Falling no longer trample farmland. |
| 🏹 | **Infinity + Mending** | Both on one bow, as on Bedrock. A built-in data pack you can turn off per world. |
| 🧟 | **Rotten flesh → leather** | Smoke it or smelt it. Also a toggleable data pack. |
| 🧱 | **Terracotta slabs & stairs** | 34 new blocks for every terracotta color, matching 26.3's concrete and wool sets. |

Tree felling and vein mining cost durability and hunger for every block, like normal mining, and stop before your tool breaks.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) **0.20.1 or newer** for Minecraft **26.3**.
2. Download **[Fabric API](https://modrinth.com/mod/fabric-api)** and Fill Vanilla Gaps from [Releases](https://github.com/wiks-sq/fillvanillagaps/releases) or [Modrinth](https://modrinth.com/mod/fillvanillagaps).
3. Put both jars in your `.minecraft/mods` folder and start the Fabric profile.

> [!NOTE]
> A beta for the **26.4 snapshots** lives on the [`26.4` branch](https://github.com/wiks-sq/fillvanillagaps/tree/26.4) and is published as a GitHub pre-release. It targets one exact snapshot, so use the 26.3 release for normal play.

> [!IMPORTANT]
> The mod adds blocks, so in multiplayer it must be installed on **the server and every client**.

## Configuration

Every feature can be turned off in `config/fillvanillagaps.json`, which is created on first launch. Restart the game after editing it.

```json
{
  "cropRightClickHarvest": true,
  "treeFelling": true,
  "treeFellingMaxLogs": 256,
  "veinMining": true,
  "veinMiningMaxBlocks": 64,
  "fastLeafDecay": true,
  "featherFallingStopsTrampling": true,
  "anvilNoTooExpensive": true,
  "doubleDoors": true,
  "safeDeathDrops": true,
  "inventorySorting": true,
  "foodSaturationHud": true,
  "foodTooltip": true
}
```

The Infinity + Mending and rotten-flesh packs can be toggled per world: run `/datapack list` to see their IDs, then `/datapack disable <id>`.

## Building from source

You need a **Java 25 JDK**. Gradle is included as a wrapper.

```bash
git clone https://github.com/wiks-sq/fillvanillagaps.git
cd fillvanillagaps
./gradlew build
```

The jar is written to `build/libs/`. To try the mod in a development client:

```bash
./gradlew runClient
```

Game tests and a manual checklist are described in [docs/TESTING.md](docs/TESTING.md).

### Project layout

| Path | Contents |
|---|---|
| `src/main/java` | Shared and server code: features, mixins, block registration, networking |
| `src/client/java` | Sort key and buttons, saturation HUD, tooltips |
| `src/gametest/java` | Automated server-side game tests |
| `src/main/resources/resourcepacks/` | The two built-in data packs |
| `tools/` | Generators for the terracotta block JSON and the mod icon |

Since 26.1, Minecraft ships unobfuscated, so the code uses Mojang's own class and method names.

## Contributing

Bug reports, ideas and pull requests are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md). Please check the [issue tracker](https://github.com/wiks-sq/fillvanillagaps/issues) first.

## License

[MIT](LICENSE) © Jesse Saarinen. This project is not affiliated with Mojang Studios or Microsoft.
