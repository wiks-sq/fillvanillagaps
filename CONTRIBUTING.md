# Contributing

Thanks for helping. Bug reports, feature ideas and pull requests are all welcome.

## Reporting bugs and suggesting features

- Search the [issues](https://github.com/wiks-sq/fillvanillagaps/issues) first.
- Use the **Bug report** or **Feature request** form.
- For bugs, include the Minecraft, Fabric Loader, Fabric API and mod versions, and attach `logs/latest.log` or the crash report.
- A good feature fits the mod's scope: a small, everyday improvement that feels like vanilla and that players would use every session. Big content additions belong in their own mod.

## Development setup

1. Install a **Java 25 JDK**.
2. Fork and clone the repo, then run `./gradlew build`.
3. Use `./gradlew runClient` to try your change in game.
4. IntelliJ IDEA 2025.3 or newer works out of the box. Open the folder as a Gradle project.

## Pull requests

- Keep each PR to one change, and describe what it does and how you tested it.
- **Every new feature needs a config toggle** in `FvgConfig`, defaulting to on.
- Add or update a game test in `src/gametest/java` for server-side behaviour, and run the tests:

  ```bash
  ./gradlew runGameTest -PacceptMinecraftEula=true
  ```

  The flag accepts the [Minecraft EULA](https://aka.ms/MinecraftEULA) for the test server.
- Match the existing style: tabs for indentation, and mixin handler methods prefixed with `fillvanillagaps$`.
- Update `CHANGELOG.md` under an "Unreleased" heading.

By contributing, you agree that your work is licensed under the [MIT License](LICENSE).
