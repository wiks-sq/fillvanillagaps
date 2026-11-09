#!/usr/bin/env python3
"""Generates every JSON file for the terracotta slab/stair blocks.

Usage: python3 tools/gen_terracotta.py ~/.minecraft/versions/26.3/26.3.jar

The vanilla jar is only read to copy the exact stairs blockstate layout
(40 facing/half/shape variants) from minecraft:white_concrete_stairs, so the
output always matches the format of the Minecraft version you build against.
"""
import json
import pathlib
import sys
import zipfile

MOD = "fillvanillagaps"
COLORS = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
          "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"]
BASES = ["terracotta"] + [f"{c}_terracotta" for c in COLORS]

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "resources"
assets = root / "assets" / MOD
data = root / "data" / MOD
mc_data = root / "data" / "minecraft"


def write(path: pathlib.Path, obj) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + "\n")


def title(s: str) -> str:
    return " ".join(w.capitalize() for w in s.split("_"))


def unlock_advancement(recipe_id: str, base: str) -> dict:
    return {
        "parent": "minecraft:recipes/root",
        "criteria": {
            "has_the_recipe": {"conditions": {"recipes": recipe_id}, "trigger": "minecraft:recipe_unlocked"},
            f"has_{base}": {"conditions": {"items": [{"items": f"minecraft:{base}"}]},
                            "trigger": "minecraft:inventory_changed"},
        },
        "requirements": [["has_the_recipe", f"has_{base}"]],
        "rewards": {"recipes": [recipe_id]},
    }


def main() -> None:
    jar = zipfile.ZipFile(sys.argv[1])
    stairs_template = json.loads(jar.read("assets/minecraft/blockstates/white_concrete_stairs.json"))
    lang = {}
    block_ids = []

    for base in BASES:
        tex = f"minecraft:block/{base}"
        textures = {"bottom": tex, "side": tex, "top": tex}
        slab, stairs = f"{base}_slab", f"{base}_stairs"
        block_ids += [slab, stairs]
        lang[f"block.{MOD}.{slab}"] = title(slab)
        lang[f"block.{MOD}.{stairs}"] = title(stairs)

        # Block + item models (textures reuse the vanilla terracotta texture).
        write(assets / "models/block" / f"{slab}.json", {"parent": "minecraft:block/slab", "textures": textures})
        write(assets / "models/block" / f"{slab}_top.json", {"parent": "minecraft:block/slab_top", "textures": textures})
        for suffix, parent in (("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")):
            write(assets / "models/block" / f"{stairs}{suffix}.json",
                  {"parent": f"minecraft:block/{parent}", "textures": textures})
        for name in (slab, stairs):
            write(assets / "items" / f"{name}.json",
                  {"model": {"type": "minecraft:model", "model": f"{MOD}:block/{name}"}})

        # Blockstates.
        write(assets / "blockstates" / f"{slab}.json", {"variants": {
            "type=bottom": {"model": f"{MOD}:block/{slab}"},
            "type=double": {"model": f"minecraft:block/{base}"},
            "type=top": {"model": f"{MOD}:block/{slab}_top"},
        }})
        stairs_state = json.loads(json.dumps(stairs_template).replace(
            "minecraft:block/white_concrete_stairs", f"{MOD}:block/{stairs}"))
        write(assets / "blockstates" / f"{stairs}.json", stairs_state)

        # Loot tables (same shape as vanilla concrete slabs/stairs).
        write(data / "loot_table/blocks" / f"{slab}.json", {
            "type": "minecraft:block",
            "pools": [{"entries": [{"type": "minecraft:item", "modifier": [
                {"type": "minecraft:set_count", "condition": {"type": "minecraft:match_block",
                                                              "blocks": f"{MOD}:{slab}",
                                                              "state": {"type": "double"}}, "count": 2},
                {"type": "minecraft:explosion_decay"}], "name": f"{MOD}:{slab}"}], "rolls": 1}],
            "random_sequence": f"{MOD}:blocks/{slab}"})
        write(data / "loot_table/blocks" / f"{stairs}.json", {
            "type": "minecraft:block",
            "pools": [{"condition": {"type": "minecraft:survives_explosion"},
                       "entries": [{"type": "minecraft:item", "name": f"{MOD}:{stairs}"}], "rolls": 1}],
            "random_sequence": f"{MOD}:blocks/{stairs}"})

        # Recipes: crafting table + stonecutter, like vanilla concrete.
        recipes = {
            slab: {"type": "minecraft:crafting_shaped", "category": "building", "group": "terracotta_slab",
                   "key": {"#": f"minecraft:{base}"}, "pattern": ["###"],
                   "result": {"count": 6, "id": f"{MOD}:{slab}"}},
            stairs: {"type": "minecraft:crafting_shaped", "category": "building", "group": "terracotta_stairs",
                     "key": {"#": f"minecraft:{base}"}, "pattern": ["#  ", "## ", "###"],
                     "result": {"count": 4, "id": f"{MOD}:{stairs}"}},
            f"{slab}_from_{base}_stonecutting": {"type": "minecraft:stonecutting", "ingredient": f"minecraft:{base}",
                                                 "result": {"count": 2, "id": f"{MOD}:{slab}"}},
            f"{stairs}_from_{base}_stonecutting": {"type": "minecraft:stonecutting", "ingredient": f"minecraft:{base}",
                                                   "result": {"id": f"{MOD}:{stairs}"}},
        }
        for rid, recipe in recipes.items():
            write(data / "recipe" / f"{rid}.json", recipe)
            write(data / "advancement/recipes/building_blocks" / f"{rid}.json",
                  unlock_advancement(f"{MOD}:{rid}", base))

    # Tags so the blocks behave like every other slab/stair (pickaxe mining, etc.).
    slabs = [f"{MOD}:{b}_slab" for b in BASES]
    stairs_ids = [f"{MOD}:{b}_stairs" for b in BASES]
    write(mc_data / "tags/block/slabs.json", {"values": slabs})
    write(mc_data / "tags/block/stairs.json", {"values": stairs_ids})
    write(mc_data / "tags/block/mineable/pickaxe.json", {"values": slabs + stairs_ids})
    write(mc_data / "tags/item/slabs.json", {"values": slabs})
    write(mc_data / "tags/item/stairs.json", {"values": stairs_ids})

    lang_path = assets / "lang/en_us.json"
    existing = json.loads(lang_path.read_text()) if lang_path.exists() else {}
    existing.update(lang)
    write(lang_path, dict(sorted(existing.items())))
    print(f"generated {len(block_ids)} blocks")


if __name__ == "__main__":
    main()
