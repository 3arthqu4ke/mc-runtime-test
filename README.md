# MC-Runtime-Test
This project showcases how to run the Minecraft client headlessly in a CI/CD pipeline to conduct tests on it.

When coding Minecraft mods many bugs can only be caught by launching the entire single-player client and joining a world.
Doing this manually, especially when developing against multiple minecraft versions and/or modloaders is laborious.
Additionally, many (e.g. mapping or mixin related) bugs only occur when running against an instance that has been launched by a launcher, instead of some IDE or gradle task.
Of course the idea to automate tests against running Minecraft clients/servers is not new, to name a few:
Microsoft offers the GameTest framework, though I do not know Java Edition developers that use it and whether its usable in a CI/CD pipeline.
SpongePowereds [McTester](https://github.com/SpongePowered/McTester) exists, but is now archived.

The issue with running the client in a CI/CD pipeline is that it is running in a headless environment, so rendering related stuff will fail.
To work around this we can provide patched versions of the rendering libraries.
As far as I know, [baritone](https://github.com/cabaletta/baritone) has done this before, running the client with a patched lwjgl version on their travis-ci.
We can do this too, by using [HeadlessMC](https://github.com/3arthqu4ke/headlessmc), a launcher specifically built to launch minecraft headlessly from the command line.

This project contains a simple 1.20.4 mod, which, after the game has launched, creates a new SinglePlayer world and waits for a short amount of time after the players chunk has loaded, then quits the game.
The [fabric.yml](.github/workflows/fabric.yml) Github Action then builds the mod, and runs it on the fabric 1.20.4 client with headlessmc.
Right away, it caught a bug that I was not warned about at compile time: my mixin config still contained old deleted mixins, causing my game to crash.
A successful run can be found [here](https://github.com/3arthqu4ke/mc-runtime-test/actions/runs/8521206581/job/23338896409).

Mc-Runtime-Test currently supports the following Minecraft versions and modloaders:
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
| 1.7.18  | :white_check_mark:  | :warning:  | - |

## TODOs and thoughts
- Make Fabric/Forge not show a screen when an exception occurs, but crash properly. (https://github.com/3arthqu4ke/mc-runtime-test/actions/runs/8543522919)
- Fix status http 429, too many requests, when downloading assets (3arthqu4ke/headlessmc#136).
- Expand this. Currently this is just a 1.20.4 example. To test headlessmc's lwjgl patching capabilities I would like to launch as many different minecraft versions as possible.  
        - Can we do this while being mostly mc and mod launcher version agnostic?  
        - Can we provide some sort of framework so noone has to write their own mod which loads into a world and does stuff?  
              - Gradle plugin for sourceSet containing these runtime tests?  
              - Github Action so noone has to maintain their own huge [fabric.yml](.github/workflows/fabric.yml).
