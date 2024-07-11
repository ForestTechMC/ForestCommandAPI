# ForestCommandAPI

![badge](https://img.shields.io/github/v/release/ForestTechMC/ForestCommandAPI)
[![badge](https://jitpack.io/v/ForestTechMC/ForestCommandAPI.svg)](https://jitpack.io/#ForestTechMC/ForestCommandAPI)
![badge](https://img.shields.io/github/downloads/ForestTechMC/ForestCommandAPI/total)
![badge](https://img.shields.io/github/last-commit/ForestTechMC/ForestCommandAPI)
![badge](https://img.shields.io/github/actions/workflow/status/ForestTechMC/ForestCommandAPI/release.yml)
![badge](https://img.shields.io/codefactor/grade/github/foresttechmc/forestcommandapi)
![badge](https://img.shields.io/badge/platform-PaperMC-lightgrey)
[![badge](https://img.shields.io/discord/896466173166747650?label=discord)](https://discord.gg/2PpdrfxhD4)
[![badge](https://img.shields.io/github/license/ForestTechMC/ForestRedisAPI)](https://github.com/ForestTechMC/ForestCommandAPI/blob/master/LICENSE.txt)

Simple, but powerful annotation-based multiplatform command API inspired by many of the available APIs.

⚠️ **Project is at the beginning of development! We recommend not to use it right now!** ⚠️

## Table of contents

* [Getting started](#getting-started)
* [Usage](#usage)
* [License](#license)

## Getting started

This API works as library - it does not require to install any file to the server.

### Add ForestCommandAPI to your project

[![badge](https://jitpack.io/v/ForestTechMC/ForestCommandAPI.svg)](https://jitpack.io/#ForestTechMC/ForestCommandAPI)

First, you need to setup the dependency on the ForestCommandAPI. 
Replace **VERSION** with the version of the release.
Replace **PLATFORM** with the name of the supported platforms (currently "paper", in the future "velocity", "spigot" and "bungeecord" will follow).

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

## Usage

MyCommand.java
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

Main.java
```java
// Use onEnable or similar method based on the platform
@Override
public void onEnable() {
    CommandAPI commandAPI = new CommandAPI(this); // plugin main as the argument
    
}
```

## License
ForestCommandAPI is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/ForestTechMC/ForestCommandAPI/blob/master/LICENSE.txt) for more information.