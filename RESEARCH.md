# What players want added to vanilla Minecraft — research and scoring

Researched 8 Nov 2026 against **Java Edition 26.3** (the latest release, 15 Sep 2026).

## Method

Votes alone are a poor signal: people upvote big, exciting ideas (new mobs, biomes) they may never use twice. So every idea was scored on two separate questions:

1. **Would players actually play it?** → *Play score* = average of
   - **D, Demand**: votes on the official feedback site plus Modrinth downloads of the mods that already do it. Downloads show what players actually install and keep, not just what they vote for.
   - **P, Play frequency**: how often a normal survival player would use it (every session = 5, once per world = 1).
2. **Is it worth building as a "fill the gaps" mod?** → *Build score* = average of
   - **G, Gap**: is it still missing in 26.3? Low if vanilla already covers it or Mojang marked it *planned*.
   - **F, Vanilla fit**: keeps the game's balance and feel.
   - **C, Cost**: how cheaply it can be built well (5 = easy).

**Overall = Play × Build ÷ 5** (out of 5). It multiplies instead of averaging, so a weak axis drags the result down. A minimap players love but that fights Mojang's design and is a huge job doesn't win on popularity alone.

### Data sources
- **Official feedback site**: the 1,600 top-voted posts across 8 categories (Gameplay, Blocks/Items, UI, Mobs, Redstone, Villagers, Structures, Biomes), pulled from feedback.minecraft.net's public API, sorted by votes.
- **Modrinth**: the 300 most-downloaded mods, plus targeted searches for specific features.
- **Ground truth for "already in vanilla"**: the 26.3 client jar itself (its language file, registries and decompiled code). Notable finds: 26.3 already has concrete and wool slabs/stairs, netherite horse armor, the locator bar and `/waypoint`, bundles, copper golems, and spears. Infinity and Mending are still mutually exclusive.

## Scores

| Idea | Evidence | D | P | G | F | C | Play | Build | **Overall** | Verdict |
|---|---|---|---|---|---|---|---|---|---|---|
| Saturation / food HUD | AppleSkin 98.2M downloads | 5 | 5 | 5 | 5 | 4 | 5.0 | 4.7 | **4.67** | ✅ Built |
| Right-click crop harvest | RightClickHarvest 13.2M, Harvest With Ease 4.2M | 4 | 5 | 5 | 5 | 5 | 4.5 | 5.0 | **4.50** | ✅ Built |
| Inventory / chest sorting | Mouse Tweaks 64.1M, Inventory Profiles Next 42.2M; the most-cited QoL wish | 5 | 5 | 5 | 5 | 3 | 5.0 | 4.3 | **4.33** | ✅ Built |
| Vein mining (ores) | Veinminer 101.2M (+73.5M client addon) | 5 | 4 | 5 | 3 | 4 | 4.5 | 4.0 | **3.60** | ✅ Built (crouch-only, costs durability) |
| Double doors open together | Double Doors 9.4M | 3 | 4 | 5 | 5 | 5 | 3.5 | 5.0 | **3.50** | ✅ Built |
| Remove anvil "Too Expensive!" | 2,013 votes; Easy Anvils 17.6M, Ante 1.9M | 4 | 3 | 5 | 4 | 5 | 3.5 | 4.7 | **3.27** | ✅ Built (cap at 39 levels) |
| Tree felling | FallingTree 13.1M, Tree Harvester 7.7M; "Lumber enchantment" 1,342 votes | 4 | 4 | 5 | 3 | 4 | 4.0 | 4.0 | **3.20** | ✅ Built (crouch-only, natural trees only) |
| Terracotta slabs & stairs | "Concrete and Terracotta slabs/stairs/walls" 20,324 votes; 26.3 did concrete + wool but skipped terracotta | 4 | 3 | 4 | 5 | 4 | 3.5 | 4.3 | **3.03** | ✅ Built (34 blocks) |
| Fast leaf decay | fld 135k (also bundled in Quark etc.); needed once trees fell instantly | 2 | 4 | 5 | 5 | 5 | 3.0 | 5.0 | **3.00** | ✅ Built |
| Dynamic hand-held lights | LambDynamicLights 63.3M | 5 | 4 | 5 | 4 | 1 | 4.5 | 3.3 | **3.00** | ⏭ Next: a rendering project of its own |
| Zoom key | Zoomify 72.0M; spyglass partly covers it | 5 | 3 | 2 | 4 | 4 | 4.0 | 3.3 | **2.67** | ⏭ Next: cheap, client-only |
| Shulker box grid preview | ShulkerBoxTooltip 41.0M; vanilla already lists 5 items | 5 | 3 | 2 | 5 | 3 | 4.0 | 3.3 | **2.67** | ⏭ Next |
| Safe death drops (light "graves") | GraveStone 12.5M, Corpse 4.7M, two more at 1.3M each | 4 | 3 | 4 | 4 | 4 | 3.5 | 4.0 | **2.80** | ✅ Built |
| Infinity + Mending together | Infinity With Mending 1.19M; Bedrock already allows it | 3 | 3 | 5 | 4 | 5 | 3.0 | 4.7 | **2.80** | ✅ Built (toggleable data pack) |
| Rotten flesh → leather | JRFTL 663k + 3 similar mods ≈ 1M | 3 | 3 | 5 | 4 | 5 | 3.0 | 4.7 | **2.80** | ✅ Built (toggleable data pack) |
| Feather Falling stops crop trampling | 3,650 votes; Trample No More 1.06M | 3 | 2 | 5 | 5 | 5 | 2.5 | 5.0 | **2.50** | ✅ Built (lowest built score, but ~20 lines and no downside) |
| Waystones / teleport network | Waystones 27.6M | 5 | 3 | 5 | 2 | 2 | 4.0 | 3.0 | 2.40 | ❌ Skip: undercuts travel |
| Minimap | Xaero's Minimap 119.7M | 5 | 5 | 4 | 2 | 1 | 5.0 | 2.3 | 2.33 | ❌ Skip: Mojang chose maps + locator bar; Xaero's is excellent |
| Fruit trees | 42,926 votes (2nd most voted ever) but tiny mod uptake | 4 | 3 | 5 | 4 | 1 | 3.5 | 3.3 | 2.33 | ❌ Skip: art + worldgen heavy |
| Bed sheets with banner patterns | 36,837 votes, Mojang: not planned | 4 | 2 | 5 | 5 | 1 | 3.0 | 3.7 | 2.20 | ❌ Skip: rendering-heavy, rarely used |
| Carry On (pick up chests/mobs) | 27.8M | 5 | 3 | 5 | 2 | 1 | 4.0 | 2.7 | 2.13 | ❌ Skip: breaks balance |
| Backpacks | 33,572 votes; Traveler's 21.7M, Sophisticated 20.7M + 11.7M port | 5 | 4 | 2 | 3 | 2 | 4.5 | 2.3 | 2.10 | ❌ Skip: bundles fill most of this gap now |
| Recipe viewer | JEI 84.9M, EMI, REI | 5 | 4 | 2 | 4 | 1 | 4.5 | 2.3 | 2.10 | ❌ Skip: recipe book exists; JEI/EMI are mature |
| Seasons | 20,015 votes, not planned | 4 | 3 | 5 | 3 | 1 | 3.5 | 3.0 | 2.10 | ❌ Skip: huge, divisive |
| Crawl key | Crawl 7.9M | 3 | 2 | 5 | 4 | 3 | 2.5 | 4.0 | 2.00 | ❌ Skip (for now) |
| Sleeping bags | Comforts 24.2M; "sleep any time" not planned | 4 | 2 | 5 | 3 | 2 | 3.0 | 3.3 | 2.00 | ❌ Skip |
| New mobs (ostrich, meerkat, capybara…) | 41.7K / 34.5K / 29.9K votes; ostrich & meerkat marked *planned* | 5 | 2 | 2 | 4 | 1 | 3.5 | 2.3 | 1.63 | ❌ Skip: Mojang is on it; needs art |

## Takeaways

- **The most-voted ideas are not the most-played ones.** Fruit trees, ostriches and banner bed sheets top the vote charts, but no mod doing them has large uptake. The mods people actually keep installed are small, everyday conveniences: AppleSkin (98M), Veinminer (101M), Mouse Tweaks (64M), inventory sorting (42M).
- **The most worthwhile gaps are QoL features Mojang has never touched**: sorting, a saturation display, harvesting, vein mining and tree felling. They're used every session, they don't change what the game is about, and each one is cheap to build.
- **Mojang is slowly filling the decorative gaps itself.** 26.3 added concrete and wool slabs/stairs, so terracotta slabs/stairs are a safe, obvious add. They're registered under this mod's namespace, so they won't collide if Mojang adds them later.

## Sources
- Minecraft Feedback site, top-voted posts by topic: https://feedback.minecraft.net/hc/en-us/community/topics (public API `/api/v2/community/topics/{id}/posts.json?sort_by=votes`)
- Modrinth API, most-downloaded mods: https://api.modrinth.com/v2/search?index=downloads (and per-feature searches)
- Modrinth "Should Be in Vanilla" collection: https://modrinth.com/collection/BldwY1RM
- Sportskeeda, QoL features Minecraft needs: https://www.sportskeeda.com/minecraft/5-much-needed-quality-of-life-features-for-minecraft
- Fabric for 26.1+ (unobfuscated toolchain): https://fabricmc.net/2026/03/14/261.html
- Fabric example mod (26.3 versions): https://github.com/FabricMC/fabric-example-mod
- Minecraft 26.3 client jar (`~/.minecraft/versions/26.3/26.3.jar`): used for language keys, registries, and data formats
