# ForestCommandAPI

![badge](https://img.shields.io/github/v/release/ForestTechMC/ForestCommandAPI)
[![badge](https://jitpack.io/v/ForestTechMC/ForestCommandAPI.svg)](https://jitpack.io/#ForestTechMC/ForestCommandAPI)
![badge](https://img.shields.io/github/downloads/ForestTechMC/ForestCommandAPI/total)
![badge](https://img.shields.io/github/last-commit/ForestTechMC/ForestCommandAPI)
![badge](https://img.shields.io/github/actions/workflow/status/ForestTechMC/ForestCommandAPI/release.yml)
![badge](https://img.shields.io/codefactor/grade/github/foresttechmc/forestcommandapi)
![badge](https://img.shields.io/badge/platform-PaperMC%20%7C%20Velocity-lightgrey)
[![badge](https://img.shields.io/discord/896466173166747650?label=discord)](https://discord.gg/2PpdrfxhD4)
[![badge](https://img.shields.io/github/license/ForestTechMC/ForestRedisAPI)](https://github.com/ForestTechMC/ForestCommandAPI/blob/master/LICENSE.txt)

Simple, but powerful annotation-based multiplatform command API inspired by many of the available APIs.

⚠️ **Project is at the beginning of development! We recommend not to use it right now!** ⚠️

## Table of contents

* [Features](#features)
* [Getting started](#getting-started)
* [Usage](#usage)
* [License](#license)

## Features

Those which are not marked are planned, but not ready.

- [X] Annotation-based commands
- [X] Automated argument parsing
- [X] Automated tab-completion
- [X] Custom argument parsers
- [ ] BungeeCord support
- [ ] SpigotMC support
- [X] PaperMC support
- [X] Velocity support
- [ ] Automated help messages
- [ ] Automated debug messages

## Getting started

This API works as library - it does not require to install any file to the server.

### Add ForestCommandAPI to your project

[![badge](https://jitpack.io/v/ForestTechMC/ForestCommandAPI.svg)](https://jitpack.io/#ForestTechMC/ForestCommandAPI)

First, you need to set up the dependency on the ForestCommandAPI. 
Replace **VERSION** with the version of the release.
Replace **PLATFORM** with the name of the supported platforms (currently "paper" and "velocity", in the future "spigot" and "bungeecord" will follow).

<details>
    <summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ForestTechMC.ForestCommandAPI</groupId>
        <artifactId>PLATFORM</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```
</details>

<details>
    <summary>Gradle</summary>

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.ForestTechMC.ForestCommandAPI:PLATFORM:VERSION'
}
```
</details>

### Setting up the API

Setting up is platform specific, especially for Velocity due to its unique approach.

<details>
<summary>PaperMC</summary>

Just create a new CommandAPI instance and you're good to go.

```java
@Override
public void onEnable() {
    /* ... your other stuff ... */
    CommandAPI commandAPI = new CommandAPI(this);
    /* ... your other stuff ... */
}
```
</details>

<details>
<summary>Velocity</summary>

This is a bit tricky as internal API structure requires ProxyServer to be
accessible from within.

```java
// You need to implement ProxyServerProvider to allow CommandAPI to access the
// ProxyServer instance
@Plugin(/* your stuff here*/)
public class VelocityPlugin implements ProxyServerProvider {

    private final ProxyServer proxyServer;
    
    @Inject
    public VelocityPlugin(ProxyServer proxyServer /* ... and your other stuff...*/) {
        this.proxyServer = proxyServer;
        /* ... your other stuff... */
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        /* ... your other stuff ... */
        CommandAPI commandAPI = new CommandAPI(this);
        /* ... your other stuff ... */
    }

    // Implementing #getProxyServer to provide ProxyServer instance
    @Override
    public void getProxyServer() {
        return proxyServer;
    }
}
```
</details>

## Usage

Making command class
```java
// Use onEnable or similar method based on the platform
@Command(name = "mycmd")
public class MyCommand implements CommandProcessor {
    
    @SubCommand
    public void defaultNoArgs(CommandSender commandSender) {
        commandSender.sendMessage("Command with no args!");
    } 
    
    @SubCommand(names = "first")
    public void prefixNoArgs(CommandSender commandSender) {
        commandSender.sendMessage("Sub-command with no args!");
    }
    
    @SubCommand(names = "second")
    public void prefixWithArgs(CommandSender commandSender, @Arg(name="arg1") String arg1, @Arg(name="int-arg", required=false) Integer arg2) {
        commandSender.sendMessage("Sub-command with args: " + arg1 + " " + arg2);
    }
    
}
```

Registering the command
```java
CommandAPI commandAPI = new CommandAPI(pluginMain); // plugin main as the argument
commandAPI.registerCommand(new MyCommand());
```

## License
ForestCommandAPI is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/ForestTechMC/ForestCommandAPI/blob/master/LICENSE.txt) for more information.