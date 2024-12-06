<h1 align="center" style="font-weight: normal;"><b>MC-Runtime-Test</b></h1>
<p align="center">Run the Minecraft client inside your CI/CD pipeline.</p>
<p align="center">MC-Runtime-Test | <a href="https://github.com/3arthqu4ke/headlessmc">HMC</a> | <a href="https://github.com/3arthqu4ke/hmc-specifics">HMC-Specifics</a> | <a href="https://github.com/3arthqu4ke/hmc-optimizations">HMC-Optimizations</a></p>

<div align="center">

[![CodeFactor](https://www.codefactor.io/repository/github/headlesshq/mc-runtime-test/badge/main)](https://www.codefactor.io/repository/github/headlesshq/mc-runtime-test/overview/main)
[![GitHub All Releases](https://img.shields.io/github/downloads/headlesshq/mc-runtime-test/total.svg)](https://github.com/headlesshq/mc-runtime-test/releases)
![](https://github.com/headlesshq/mc-runtime-test/actions/workflows/run-matrix.yml/badge.svg)
![GitHub](https://img.shields.io/github/license/headlesshq/mc-runtime-test)
![Github last-commit](https://img.shields.io/github/last-commit/headlesshq/mc-runtime-test)

</div>

> [!WARNING]
> NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.

This action runs the Minecraft client inside your CI/CD pipeline.

When coding Minecraft mods many bugs can only be caught at runtime.
Between many different versions and modloaders manual testing becomes laborious.
Additionally, many (e.g. mapping or mixin related) bugs only occur when running against a Minecraft instance that has been launched by a launcher, instead of some IDE or gradle task.

This action runs the Minecraft client using the [HeadlessMC](https://github.com/3arthqu4ke/headlessmc) launcher.
It uses Xvfb as a virtual framebuffer that allows us to run the game headlessly.
HeadlessMC can also be used to patch the lwjgl library.

This project also provides mods for several versions, which all do one thing: join a single-player world, wait for chunks to load,
and then quit the game after a few seconds.
This way you can already run simple boot tests, checking whether the game will boot with your mod.
Mods for newer versions also execute all [gametests](https://www.minecraft.net/en-us/creator/article/get-started-gametest-framework)
registered.

MC-Runtime-Test currently supports the following Minecraft versions and modloaders:
You can configure it to use any other version, but in that case you need to set `mc-runtime-test` to `none` and provide another way for the game to exit, or the workflow will run indefinitely.

<div align="center">
  
| Version  | Forge | Fabric | NeoForge | 
| :-: | :-: | :-: | :-: |
| 1.21 - 1.21.3  | :white_check_mark:  | :white_check_mark:  | :white_check_mark: |
| 1.20.2 - 1.20.6  | :white_check_mark:  | :white_check_mark:  | :white_check_mark: |
| 1.20.1  | :white_check_mark:  | :white_check_mark:  | :warning:  |
| 1.19 - 1.19.4  | :white_check_mark:  | :white_check_mark:  | - |
| 1.18.2  | :white_check_mark:  | :white_check_mark:  | - |
| 1.17.1  | :white_check_mark:  | :white_check_mark:  | - |
| 1.16.5  | :white_check_mark:  | :white_check_mark:  | - |
| 1.12.2  | :white_check_mark:  | :warning:  | - |
| 1.8.9  | :white_check_mark:  | :warning:  | - |
| 1.7.10  | :white_check_mark:  | :warning:  | - |

</div>

Versions marked with :warning: have not been tested yet, due to not being supported by HeadlessMC, e.g. fabric legacy versions.
<!-- x-release-please-start-version -->
# Example
```yml
name: Run the MC client
on:
  workflow_dispatch:

env:
  java_version: 21

jobs:
  run:
    runs-on: ubuntu-latest
    steps:
      - name: Install Java
        uses: useblacksmith/setup-java@v5
        with:
          java-version: ${{ env.java_version }}
          distribution: "temurin"
      # ... run actions to build your mod
      # Copy the jar that you build to the mods folder
      - name: Copy mod jar to mods
        run: mkdir -p run/mods && cp build/libs/<your-mod>.jar run/mods
      # Call this Action to run the client
      - name: Run the MC client
        uses: headlesshq/mc-runtime-test@3.0.0
        with:
          mc: 1.21.4
          modloader: fabric
          regex: .*fabric.*
          mc-runtime-test: fabric
          java: ${{ env.java_version }}
```
<!-- x-release-please-end -->
An example workflow in action can be found
[here](https://github.com/3arthqu4ke/hmc-optimizations/blob/1.20.4/.github/workflows/run-fabric.yml).
An example for a large matrix workflow
which tests 20 different versions of Minecraft
at once can be found 
[here](https://github.com/3arthqu4ke/hmc-specifics/blob/main/.github/workflows/run-matrix.yml).

# Inputs
- `mc`: The MC version to use, e.g. `1.20.4`.
- `modloader`: The modloader to install with HeadlessMC (`forge`, `neoforge` or `fabric`).
- `regex`: A Regex to match the MC version to launch (can in most cases just be `.*<modloader>.*`, like `.*fabric.*`, very old versions of forge might start with an uppercase `Forge`).
- `java`: The Java version to use, e.g. `17`.
- `dummy-assets`: HeadlessMC will use dummy assets to not download all the MC assets. Can be disabled by setting this to `false`.
- `mc-runtime-test`: The MC-Runtime-Test jar to download (`none`, `lexforge`, `fabric` or `neoforge`). When using `none` you need to provide a way for the game to exit or the action will run indefinitely and time out.
- `xvfb`: If `true` (default), runs the game with Xvfb, if false, you should probably use the -lwjgl option in headlessmc.
- `headlessmc-command`: Allows you to customize the arguments of the headlessmc command.
- `fabric-api`: Downloads the fabric-api. (Default is none, an example value would be 0.97.0, to download 0.97.0+\<mc\>)
- `fabric-gametest-api`: Downloads the fabric-gametest-api to run gametests. (Default is none, an example value would be 1.3.5+85d85a934f).
- `download-hmc`: Whether to download headlessmc or not, if not you need to supply a headlessmc-launcher.jar. Default is `true`.
- `hmc-version`: The [headlessmc version](https://github.com/3arthqu4ke/headlessmc/releases) to download.

# Running your own tests
MC-Runtime-Test does not provide a framework for full integration tests.
You can, however, use Minecrafts own [Game-Test Framework](https://www.minecraft.net/en-us/creator/article/get-started-gametest-framework).
MC-Runtime-Test will basically execute the `/test runall` command after joining the world.
On Neoforge/Lexforge gametest discovery does really not work in production, you might need to register
them themselves and use other [hacks](gametest/src/main/java/me/earth/clientgametest/mixin/MixinGameTestRegistry.java)
to get the structure templates correctly, but we are working on it.
You can also use the `headlessmc-command` input to specify additional SystemProperties with the `--jvm` flag.
E.g. `-DMcRuntimeGameTestMinExpectedGameTests=<int>` to specify how many gametests you expect to be executed
at minimum and otherwise fail if not enough gametests have been found.

# Credits
[unimined](https://github.com/unimined/unimined) by [wagyourtail](https://github.com/wagyourtail), a gradle plugin allowing us to support multiple modloaders, has greatly accelerated the development of mc-runtime-test
