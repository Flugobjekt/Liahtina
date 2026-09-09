> [!WARNING]
> ### ⚠️ Critical: Server Shutdown & Linear / b_linear Region Formats
>
> If you are using any Linear region format (**Linear v1, v2, v3** or **b_linear**), **NEVER force-kill (`kill -9`, `SIGKILL`, panel crash/stop buttons)** the server process!
>
> Always shut down or restart cleanly using `/stop` or `/restart`.
>
> **Known risks and limitations with Linear formats:**
> - **Catastrophic Chunk / Region Corruption:** Linear formats buffer and compress chunks into shared Zstandard/LZ4 data blocks rather than separate Anvil `.mca` sector slots. Abruptly terminating the process while a flush or write-ahead-log (WAL) sync is in progress can corrupt entire region files (up to 1,024 chunks at once).
> - **Data Loss on Hard Crashes:** If the host machine loses power, suffers an OOM-killer termination, or is abruptly stopped without executing shutdown hooks, unwritten in-memory chunk buffers cannot be recovered.
> - **Incompatibility & Tooling Issues:** Linear v3 and non-standard specifications are not supported by external tools (such as map renderers like Dynmap/BlueMap, world converters, or NBT editors). Always keep reliable backups before migrating.

<div align="center">
  <h1>Liahtina</h1>
  <p><strong>A high-performance fork of Folia</strong></p>
  <p>Tracking Mojang's latest Minecraft versions with Bukkit plugin compatibility and vanilla feature restoration</p>

  <p>
    <a href="https://github.com/Flugobjekt/Liahtina/actions/workflows/build.yml"><img src="https://github.com/Flugobjekt/Liahtina/actions/workflows/build.yml/badge.svg" alt="Actions Build Status"></a>
    <a href="https://github.com/Flugobjekt/Liahtina/issues"><img src="https://img.shields.io/github/issues/Flugobjekt/Liahtina" alt="GitHub Issues"></a>
    <a href="https://github.com/Flugobjekt/Liahtina/commits"><img src="https://img.shields.io/github/last-commit/Flugobjekt/Liahtina" alt="Last Commit"></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/badge/License-GPL--3.0-blue" alt="License"></a>
  </p>
</div>

---


## Disclaimer

> The original Luminol project was developed by **EarthME**, but was archived due to personal reasons. This version aims to adapt to Mojang's latest Minecraft versions.
>
> Due to the **GNU GPL V3.0** open source license requirements, any modifications, derivatives, or distributions based on this project must be released under the same open source license in full.

---

## Features

| Category | Feature | Description | Status |
|----------|---------|-------------|--------|
| **Compat** | Bukkit Plugin Compat | `FoliaSchedulerCompatibility` auto-detects plugin scheduling needs, bridges to Folia region scheduler or falls back | Implemented |
| **Compat** | Async Protocol Switch | Async login/config/game protocol switching, reducing main thread blocking | Implemented |
| **Config** | Complete Config Framework | TOML config auto-loading, structured configuration support, custom brand names | Implemented |
| **Fixes** | Collision Behavior | Configurable VANILLA / PAPER / BLOCK_SHAPE_VANILLA collision detection | Implemented |
| **Fixes** | Pathfinding Fixes | Detects cross-region pathfinding and delays recomputation | Implemented |
| **Fixes** | High Velocity Fix | Detects cross-region entity movement and handles via teleportAsync | Implemented |
| **Fixes** | POI Range Fix | Avoids loading distant POI chunks | Implemented |
| **Fixes** | Item Multitask | Allows item usage during block interaction (Crystal PVP) | Implemented |
| **Fixes** | Vanilla Random Source | Optional restore of independent entity random source | Implemented |
| **Fixes** | Memory Cleanup | Auto-clears entity/blockpos/position memories outside current region | Implemented |
| **Fixes** | teleportAsync Guard | Prevents incorrect teleportAsync calls during move events | Implemented |
| **Fixes** | Movement Warnings | Configurable disable of moved too quickly / moved wrongly warnings | Implemented |
| **Perf** | Lobotomize Villagers | Detects stuck villagers and skips Brain AI tick | Implemented |
| **Perf** | Sensor Throttling | Reduces entity sensor tick frequency (default every 10 ticks) | Implemented |
| **Perf** | Goal Selector Throttle | Inactive entity AI goal selector runs every 20 ticks | Implemented |
| **Perf** | Projectile Chunk Limit | Limits chunk loading triggered by projectiles | Implemented |
| **Perf** | Variable Entity Wakeup | Gaussian-distributed wakeup timing to avoid mass wakeups | Implemented |
| **Perf** | Dragon Respawn Optimize | Cached search positions for faster ender dragon respawn | Implemented |
| **Perf** | SIMD Vectorization | Detects CPU SIMD support, auto-enables vectorized operations | Implemented |
| **Perf** | CPU Affinity | Thread-to-core binding for big.LITTLE optimization | Implemented |
| **Feature** | Tripwire Dupe | Configurable tripwire behavior (VANILLA20/21/MIXED) | Implemented |
| **Feature** | Region Formats | Configurable chunk storage: MCA, Linear v2 (fixed bitmap), Linear v3 (header v4), Buffered Linear (B_LINEAR) | Implemented |
| **Feature** | Portal Rate Limiter | Limits portal teleportations per tick | Implemented |
| **Feature** | Command Block Toggle | Enable/disable command blocks via config | Implemented |
| **Feature** | Disable Async Catchers | Disable Folia thread safety checks (experimental) | Implemented |
| **Feature** | Disable Entity Exception | Entity tick exceptions thrown instead of silent removal | Implemented |
| **Arch** | Regionized Multithreading | Folia-based regionized threading model | Implemented |
| **Arch** | Region Data Management | Reference-counted regionized world data pool | Implemented |
| **Monitor** | Performance Monitoring | Region Profiler, Watchdog thread, chunk throughput counters | Implemented |

---

## Tech Stack

| Component | Version | Description |
|-----------|---------|-------------|
| **Minecraft** | 26.2 | Upstream Mojang version |
| **Folia** | 26.2 | Regionized multi-threaded server core |
| **Paper** | `1569b8dc` ref | Upstream Paper commit |
| **Luminol** | `ba28403f` ref | Reference commit |
| **Arbor** | 26.2 | Feature reference and code porting |
| **Java** | 25 | Compile and runtime JDK |
| **Gradle** | 9.x | Build tool (wrapper included) |
| **Paperweight Patcher** | 2.0.0-beta.21 | Patch application and project management plugin |
| **Mache** | bundled with paperweight | Mojang mapping decompiler |
| **CI/CD** | GitHub Actions (windows-latest) | Windows build environment |
| **Git** | >= 2.x | Required by the patch system |

---

## Building

### Prerequisites

- **JDK 25** (Zulu or GraalVM recommended)
- **Git 2.x+**
- **Windows 10/11** or **Linux/macOS** (this project's CI uses Windows)
- Network access to GitHub, Mojang official sources, and the PaperMC Maven repository
- **Gradle** wrapper is included (no separate installation required)

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/Flugobjekt/Liahtina.git
cd Liahtina

# Apply all patches
.\gradlew.bat applyAllPatches          # Windows
./gradlew applyAllPatches              # Linux / macOS

# REQUIRED: restore committed source modifications
# applyAllPatches overwrites src/minecraft/java with decompiled sources — DO NOT SKIP this step!
git checkout -- liahtina-server/src/minecraft/java/   # required on all platforms

# Build the runnable Paperclip JAR
.\gradlew.bat createPaperclipJar       # Windows
./gradlew createPaperclipJar           # Linux / macOS
```

The `applyAllPatches` task will: pull upstream Paper 26.2 source via `paperRef` → apply `liahtina-api/paper-patches` → apply `liahtina-server/{paper,minecraft,luminol}-patches` → merge the core sources under `src/main/java` into the compile path.

> **Important** : `applyAllPatches` resets `liahtina-server/src/minecraft/java/` to the upstream decompiled sources. Any changes committed directly in that directory will be lost. **You MUST run `git checkout -- liahtina-server/src/minecraft/java/` BEFORE `createPaperclipJar`**, otherwise the built JAR will be missing these modifications.

### One-click Build

```bash
# Run each command separately:
.\gradlew.bat applyAllPatches
git checkout -- liahtina-server/src/minecraft/java/
.\gradlew.bat createPaperclipJar
```

### Available Gradle Tasks

| Task | Description |
|------|-------------|
| `applyAllPatches` | Apply all patches to the upstream source |
| `createPaperclipJar` | Build the runnable Paperclip JAR (recommended for production) |
| `createBundlerJar` | Build a Bundler JAR (includes all dependencies) |
| `:liahtina-server:jar` | Compile and package server classes only (no dependencies) |
| `:liahtina-server:compileJava` | Compile Java sources only (compile validation) |
| `runPaperclip` | Spin up a test server directly |
| `rebuildPatches` | Regenerate patch files from the current source |

### Build Output

The runnable Paperclip JAR is located at:

```
liahtina-server/build/libs/liahtina-paperclip-26.2.0-R0.1-SNAPSHOT.jar
```

### Deploy & Run

Rename the Paperclip JAR and drop it into your server directory:

```bash
copy liahtina-paperclip-26.2.0-R0.1-SNAPSHOT.jar D:\SkyServer\liahtina-26.2.jar
```

```bat
@echo off
java -Xms1024M -Xmx1024M -jar liahtina-26.2.jar --nogui
pause
```

### Patch System

This project maintains three patch layers via Paperweight, all under `liahtina-server/`:

| Patch Directory | Count | Main Contents |
|-----------------|-------|---------------|
| `paper-patches/features/` | 10 | Region threading base, logo, build changes, region profiler, watchdog, TPS, rebrand, dirty patches, Bukkit scheduler compat |
| `minecraft-patches/features/` | 13 | Region threading base, login queue, chunk counters, block update protection, entity read protection, vehicle sync, watchdog, teleport fixes, scheduler compat, region data exposure |
| `luminol-patches/features/` | 4 | Rebrand, Folia flag check, auto-update, scheduler compat |

> Patches are applied in numbered order. To modify a patch, use the `rebuildPatches` task to regenerate it.

---

## Configuration

On first startup, the server automatically generates a `liahtina_config/` configuration directory.

### Configuration Files

| File | Description |
|------|-------------|
| `server_mod_name.yml` | Server brand name displayed in the F3 debug screen |
| `disable_check_for_folia_supported.yml` | Controls the Folia plugin support check bypass |
| `folia_scheduler_compatibility.yml` | Controls scheduler routing for legacy plugins |
| `config.toml` (`[function.region_format]`) | Region file format (`MCA`, `LINEAR_V2`, `LINEAR_V3`, `B_LINEAR`) and IO settings |

### Example: `config.toml` (`[function.region_format]`)

```toml
[function.region_format]
# Region file format to use for world saving:
# - MCA: Standard Minecraft Anvil format (.mca)
# - LINEAR_V2: Linear v2 with bucket compression and bitmap (.linear)
# - LINEAR_V3: Linear v3 with reduced header overhead and direct bucket hashes (.linear)
# - B_LINEAR: Buffered Linear region format with asynchronous flush (.b_linear)
format = "MCA"

# Compression level (1-22, default 1) for Linear and B_LINEAR
linear_compression_level = 1

# Flush delay in milliseconds (default 100) for Linear v2 / v3
linear_io_flush_delay_ms = 100

# Worker thread count (default 6) for Linear IO
linear_io_thread_count = 6

# Use virtual threads for Linear IO
linear_use_virtual_thread = true

# Flush delay in milliseconds (default 3000) for B_LINEAR
blinear_io_flush_delay_ms = 3000

# Worker thread count (default 6) for B_LINEAR
blinear_io_thread_count = 6
```

### Example: `server_mod_name.yml`

```yaml
# Server brand name shown in F3 debug overlay
name: Liahtina
# Force vanilla brand name (overrides plugins and this config)
vanilla_spoof: false
```

### Example: `disable_check_for_folia_supported.yml`

```yaml
# Bypass Folia support check for Spigot/Bukkit/Paper plugins
disable_for_paper: true
# Bypass Folia support check for Leaves plugins
disable_for_leaves: true
```

### Example: `folia_scheduler_compatibility.yml`

```yaml
# Enable automatic scheduler routing based on plugin metadata
enabled: true
# Force specific plugins to use the Folia scheduler
force_folia_scheduler_plugins: []
# Force specific plugins to use the Bukkit scheduler
force_bukkit_scheduler_plugins: []
```

---

## License

This project is licensed under the **GNU General Public License v3.0**. See [LICENSE.md](LICENSE.md) for the full license text.

> **GPLv3 Notice:** Any modifications, derivatives, or distributions of this project must be released under the same GPLv3 license with full source code disclosure.

---

## Acknowledgments

| Contributor | Role |
|-------------|------|
| **EarthME** | Original Luminol project author |
| **PaperMC** | Folia and Paper server framework |
| **Arbor** | Secondary development, providing feature reference and code porting |
| **Liahtina** | Vanilla feature restoration layer |
| **Flugobjekt** | Project maintainer |

---

<div align="center">
  <p>
    <a href="https://github.com/Flugobjekt/Liahtina">Repository</a>
    &nbsp;&middot;&nbsp;
    <a href="https://github.com/Flugobjekt/Liahtina/issues">Issue Tracker</a>
    &nbsp;&middot;&nbsp;
    <a href="https://github.com/Flugobjekt/Liahtina/releases">Releases</a>
  </p>
</div>
