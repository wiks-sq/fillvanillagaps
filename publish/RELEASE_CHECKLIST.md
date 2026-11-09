# Release checklist

## Before uploading

1. Set `JAVA_HOME` so Gradle uses the launcher's Java 25:

   ```bash
   export JAVA_HOME=~/.minecraft/runtime/java-runtime-epsilon/linux/java-runtime-epsilon
   ```

2. Run the game tests (this accepts the Minecraft EULA for the test server):

   ```bash
   ./gradlew runGameTest -PacceptMinecraftEula=true
   ```

3. Build the jar:

   ```bash
   ./gradlew build
   ```

   The jar to upload is `build/libs/fillvanillagaps-<version>.jar`.

4. Launch the game once to check everything loads:

   ```bash
   ./gradlew runClient
   ```

5. Take 3–5 screenshots for the gallery: sorting a chest, the saturation outline, a tree falling, the terracotta blocks.

## Creating the Modrinth project (first time only)

| Field | Value |
|---|---|
| Name / URL | Fill Vanilla Gaps / `fillvanillagaps` |
| Summary | The small, long-requested features vanilla still lacks. |
| Description | Paste `publish/MODRINTH.md` |
| Categories | Utility, Game Mechanics, Storage, Food |
| Client / Server | Required / Required |
| License | MIT |
| Source | Your GitHub repo URL. Also add it as `"sources"` under `contact` in `src/main/resources/fabric.mod.json`. |
| Icon | `src/main/resources/assets/fillvanillagaps/icon.png` |

## Uploading a version

| Field | Value |
|---|---|
| Version number | `1.0.0+26.3` |
| Release channel | Release |
| Loader | Fabric |
| Game version | 26.3 |
| Dependency | Fabric API (required) |
| Changelog | Copy the matching section of `CHANGELOG.md` |
| File | `build/libs/fillvanillagaps-1.0.0+26.3.jar` |

## Updating for a new Minecraft version

1. Update `minecraft_version`, `loader_version`, `fabric_api_version` and `loom_version` in `gradle.properties`. Copy the values from https://fabricmc.net/develop or the Fabric example mod.
2. Update `version`, for example to `1.0.1+26.4`, and the `"minecraft"` range in `fabric.mod.json`.
3. Fix any compile errors, then go through "Before uploading" again.
