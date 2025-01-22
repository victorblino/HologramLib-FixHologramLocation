</div>
<div align="center">
<p><b>FOLIA ✅ PAPER ✅ PURPUR ✅ 1.19.4 - 1.21 ✅</b></p>
<a href="https://discord.gg/2UTkYj26B4" target="_blank"><img src="https://img.shields.io/badge/Discord_Server-7289DA?style=flat&logo=discord&logoColor=white" alt="Join Discord Server" style="border-radius: 15px; height: 20px;"></a>
<a href="https://github.com/max1mde/ExampleHologramPlugin"><img src="https://img.shields.io/badge/Example%20plugin-13B8E1" alt="Version"></a>
<a href="https://jitpack.io/#max1mde/HologramLib"><img src="https://jitpack.io/v/max1mde/HologramLib.svg" alt="jitpack"></a> 
<a href="https://github.com/max1mde/HologramLib/releases"><img src="https://img.shields.io/github/downloads/max1mde/HologramLib/total.svg" alt="Downloads"></a>
<br>
<img width="600px" src="assets/banner.png">
<p>Leave a :star: if you like this library :octocat:</p>
</div>

<div>
<h3>Contents</h3>
• <a href="#installation">Installation</a><br>
• <a href="#exampleshowcase-plugin">Example plugin with which uses HologramLib</a>
<br>
<br>
<details>
<summary><a href="#first-steps">First Steps</a></summary>
&nbsp;&nbsp;&nbsp;• <a href="#initializing-hologrammanager">Initializing Manager</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-rendering-modes">Rendering Modes</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-creation">Creation</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-animation">Text Animations</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#leaderboard-creation">Leaderboards</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#setting-a-hologram-as-a-passenger">Passengers</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#managing-hologram-viewers">Viewers</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#advanced-transformations">Transformations</a><br>
&nbsp;&nbsp;&nbsp;• <a href="#hologram-retrieval-and-management">Management</a>
</details>
</div>

# Features
- Text, Block & Item Holograms
- Text animations
- Minimessage support
- Packet based
- Per player holograms
- Leaderboard generators
- Advanced hologram customization
- Attachment and parenting support
- Flexible rendering modes

# Installation

- Download packet events https://www.spigotmc.org/resources/80279/
- Download HologramLib-[version]**.jar** file from the [latest release](https://github.com/max1mde/HologramLib/releases)
- Upload the HologramLib-[version]**.jar** and packet events file on your server (_yourserver/**plugins**_ folder)
- Add the plugin as a dependency to your plugin and use it

> [!NOTE]  
> You might also want to add [packetevents](https://github.com/retrooper/packetevents/wiki/Depending-on-pre%E2%80%90built-PacketEvents) as a compile-only dependency to your plugin  
> if you want to use (import) certain features, such as the `ItemStack` for the item hologram.

**Gradle installation**
```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  compileOnly 'com.github.max1mde:HologramLib:1.6.7'
}
```
**Maven installation**
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.max1mde</groupId>
  <artifactId>HologramLib</artifactId>
  <version>1.6.7</version>
  <scope>provided</scope>
</dependency>
```
Add this to your plugin
`plugin.yml`
```yml
depend:
  - HologramLib
```

> [!IMPORTANT]  
> Using `minimize()` in your shadow jar configuration could break HologramLib.

# Example/Showcase Plugin
https://github.com/max1mde/ExampleHologramPlugin

# First Steps

### Initializing HologramManager
```java
private HologramManager hologramManager;

@Override
public void onLoad() {
    HologramLib.onLoad(this); /*Only needed if you shade HologramLib*/
}

@Override
public void onEnable() {
    HologramLib.getManager().ifPresentOrElse(
        manager -> hologramManager = manager,
        () -> getLogger().severe("Failed to initialize HologramLib manager.")
    );
}
```

# Continue on the [wiki page](https://github.com/max1mde/HologramLib/wiki)

## Projects using HologramLib
- [TypingInChat Plugin](https://modrinth.com/plugin/typinginchat-plugin) by [Orphey](https://github.com/Orphey98)

Contributions to this repo or the example plugin are welcome!
