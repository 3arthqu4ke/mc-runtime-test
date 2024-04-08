# MC-Runtime-Test
> [!WARNING]
> NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.

This action runs the Minecraft client inside your CI/CD pipeline.

When coding Minecraft mods many bugs can only be caught at runtime.
Between many different versions and modloaders manual testing becomes laborious.
Additionally, many (e.g. mapping or mixin related) bugs only occur when running against a Minecraft instance that has been launched by a launcher, instead of some IDE or gradle task.

This action runs the Minecraft client using the [HeadlessMC](https://github.com/3arthqu4ke/headlessmc) launcher.
It uses Xvfb as a virtual framebuffer that allows us to run the game headlessly.
HeadlessMC can also be used to patch the lwjgl library.
It also provides mods for several versions, which all do one thing: join a single-player world, wait for chunks to load, and then quit the game after a few seconds.
This way you can already run simple boot tests, checking whether the game will boot with your mod.

Mc-Runtime-Test currently supports the following Minecraft versions and modloaders:
You can configure it to use any other version, but in that case you need to set `mc_runtime_test` to `none` and provide another way for the game to exit, or the workflow will run indefinitely.
| Version  | Forge | Fabric | NeoForge | 
| :-: | :-: | :-: | :-: |
| 1.20.2 - 1.20.4  | :white_check_mark:  | :white_check_mark:  | :white_check_mark: |
| 1.20.1  | :white_check_mark:  | :white_check_mark:  | :warning:  |
| 1.19 - 1.19.4  | :white_check_mark:  | :white_check_mark:  | - |
| 1.18.2  | :white_check_mark:  | :white_check_mark:  | - |
| 1.17.1  | :white_check_mark:  | :white_check_mark:  | - |
| 1.16.5  | :white_check_mark:  | :white_check_mark:  | - |
| 1.12.2  | :white_check_mark:  | :warning:  | - |
| 1.8.9  | :white_check_mark:  | :warning:  | - |
| 1.7.10  | :white_check_mark:  | :warning:  | - |

Versions marked with :warning: have not been tested yet, due to not being supported by HeadlessMC, e.g. fabric legacy versions.

# Example
```yml
name: Run the MC client
on:
  workflow_dispatch:

jobs:
  run:
    runs-on: ubuntu-latest
    steps:
      # ... run actions to build your client
      # Copy the jar that you build to the mods folder
      - name: Copy mod jar to mods
        run: mkdir -p run/mods && cp build/libs/<your-mod>.jar run/mods
      # Call this Action to run the client
      - name: Run the MC client
        uses: 3arthqu4ke/mc-runtime-test@1.1.0
        with:
          mc: 1.20.4
          modloader: fabric
          regex: .*fabric.*
          java: 17
```
An example workflow in action can be found [here](https://github.com/3arthqu4ke/hmc-optimizations/blob/1.20.4/.github/workflows/run-fabric.yml).

# Inputs
- `mc`: The MC version to use, e.g. `1.20.4`.
- `modloader`: The modloader to install with HeadlessMC (`forge`, `neoforge` or `fabric`).
- `regex`: A Regex to match the MC version to launch (can in most cases just be `.*<modloader>.*`, like `.*fabric.*`, very old versions of forge might start with an uppercase `Forge`).
- `java`: The Java version to use, e.g. `17`, we use the adopt distribution.
- `dummy_assets`: HeadlessMC will use dummy assets to not download all the MC assets. Can be disabled by setting this to `false`.
- `mc_runtime_test`: The MC-Runtime-Test jar to download (`none`, `lexforge`, `fabric` or `neoforge`). When using `none` you need to provide a way for the game to exit or the action will run indefinitely and time out.
- `xvfb`: If `true` (default), runs the game with Xvfb, if false, you should probably use the -lwjgl option in headlessmc.
- `headlessmc_command`: Allows you to customize the arguments of the headlessmc command.

# Running your own tests
MC-Runtime-Test does not provide a framework for proper game tests (yet?).
With the mc-runtime-test mod jars you can only test if your game can boot into a world.
To write your own tests you need to build a mod that contains some form of tests, e.g. checking some value each tick, and put that jar in `run/mods` inside your workflow.
One idea for to help with this be to use a @Pseudo mixin on the McRuntimeTest class.

# Credits
[unimined](https://github.com/unimined/unimined) by [wagyourtail](https://github.com/wagyourtail), a gradle plugin allowing us to support multiple modloaders, has greatly accelerated the development of mc-runtime-test
