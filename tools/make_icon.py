#!/usr/bin/env python3
"""Draws the 128x128 mod icon: a 3x3 inventory grid with the one missing slot being filled in gold."""
import pathlib
from PIL import Image, ImageDraw

S = 32  # drawn at 32x32, scaled up 4x with nearest-neighbour for crisp pixels
img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(img)
d.rounded_rectangle((0, 0, S - 1, S - 1), radius=4, fill=(58, 62, 74, 255), outline=(24, 26, 32, 255))
items = [(0x5d, 0x9e, 0x40), (0x8b, 0x5a, 0x2b), (0xc6, 0x6b, 0x3d),   # grass, wood, terracotta
         (0xe8, 0xe2, 0xc4), None, (0x6f, 0xd0, 0xd8),                 # bone, (gap), diamond
         (0x9a, 0x9a, 0xa0), (0xd9, 0x4c, 0x3a), (0x3f, 0x76, 0xe4)]   # stone, apple, lapis
for i, color in enumerate(items):
    x, y = 3 + (i % 3) * 9, 3 + (i // 3) * 9
    d.rectangle((x, y, x + 7, y + 7), fill=(139, 139, 139, 255))          # slot
    d.line((x, y, x + 7, y), fill=(55, 55, 55, 255)); d.line((x, y, x, y + 7), fill=(55, 55, 55, 255))
    d.line((x, y + 7, x + 7, y + 7), fill=(255, 255, 255, 255)); d.line((x + 7, y, x + 7, y + 7), fill=(255, 255, 255, 255))
    if color:
        d.rectangle((x + 2, y + 2, x + 5, y + 5), fill=color + (255,))
        d.point((x + 2, y + 2), fill=tuple(min(255, c + 60) for c in color) + (255,))
    else:  # the gap, being filled: a glowing gold block with a sparkle
        d.rectangle((x + 1, y + 1, x + 6, y + 6), fill=(250, 205, 60, 255))
        d.rectangle((x + 2, y + 2, x + 5, y + 5), fill=(255, 235, 120, 255))
        d.point((x + 3, y + 3), fill=(255, 255, 255, 255))
        for px, py in ((x - 1, y - 1), (x + 8, y - 1), (x - 1, y + 8), (x + 8, y + 8)):
            d.point((px, py), fill=(255, 240, 150, 255))
out = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/fillvanillagaps/icon.png"
img.resize((128, 128), Image.NEAREST).save(out)
print("wrote", out)
