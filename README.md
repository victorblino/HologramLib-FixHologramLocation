<div align="center">
  <img width="650px" src="assets/banner.png" alt="HologramLib Banner">
  
  [![Discord](https://img.shields.io/badge/Discord_Server-7289DA?style=flat&logo=discord&logoColor=white)](https://discord.gg/2UTkYj26B4)
  [![Wiki](https://img.shields.io/badge/Documentation-Wiki-2dad10)](https://github.com/max1mde/HologramLib/wiki)
  [![JitPack](https://jitpack.io/v/max1mde/HologramLib.svg)](https://jitpack.io/#max1mde/HologramLib)
  [![JavaDocs](https://img.shields.io/badge/API-Docs-2ECC71)](https://max1mde.github.io/HologramLib/)
  [![GitHub Downloads](https://img.shields.io/github/downloads/max1mde/HologramLib/total?color=2ECC71)](https://github.com/max1mde/HologramLib/releases)


  <p>Leave a :star: if you like this library :octocat:</p>
  <h3>Next-Gen Hologram Library for Modern Minecraft Servers</h3>
  <p>Packet-based • Feature-rich • Developer-friendly</p>
</div>

---

## 🫨 Features
- **Multi-Type Holograms**    
Text • Blocks • Items • Leaderboards  

- **Dynamic Content**  
Live animations • MiniMessage formatting • ItemsAdder emojis

- **Advanced Mechanics**  
Entity attachment • Per-player visibility • View distance control    

---


## ⚙️ Technical Specifications

**Compatibility**  
| Server Software | Minecraft Versions       | 
|-----------------|--------------------------|
| **Paper**       | 1.19.4 → 1.21.4 ✔️       |
| **Purpur**      | 1.19.4 → 1.21.4 ✔️       | 
| **Folia**       | 1.19.4 → 1.21.4 ✔️       | 
| **Spigot**      | 1.19.4 → 1.21.4 ✔️       | 
| **Bedrock**     | ❌ Not supported         | 
| **Legacy**      | ❌ (1.8 - 1.19.3)        | 

**Dependencies**  
- [PacketEvents](https://www.spigotmc.org/resources/80279/) (Required)

If you want to learn how to use HologramLib in your plugin, check out the detailed guide here:  
👉 [HologramLib Wiki](https://github.com/max1mde/HologramLib/wiki)

---

## ✈️ Quick Integration

**Step 1: Add Dependency**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.max1mde:HologramLib:1.6.8'
}
```

**Step 2: Basic Implementation**
```java
HologramManager manager = HologramAPI.getManager().get();

TextHologram hologram = new TextHologram("unique_id")
    .setMiniMessageText("<aqua>Hello world!")
    .setSeeThroughBlocks(false)
    .setShadow(true)
    .setScale(1.5F, 1.5F, 1.5F)
    .setTextOpacity((byte) 200)
    .setBackgroundColor(Color.fromARGB(60, 255, 236, 222).asARGB())
    .setMaxLineWidth(200);

manager.spawn(hologram);
```

---

## 📕 Learning Resources

<img width="536px" src="https://github.com/user-attachments/assets/e4d108d3-e6cb-4d33-b91b-aa989e5e4475" alt="HologramLib Banner">

| Resource | Description | 
|----------|-------------|
| [📖 Complete Wiki](https://github.com/max1mde/HologramLib/wiki) | Setup guides • Detailed examples • Best practices |
| [💡 Example Plugin](https://github.com/max1mde/ExampleHologramPlugin) | Production-ready implementations |
| [🎥 Tutorial Series](https://github.com/max1mde/HologramLib) | Video walkthroughs (Coming Soon) |

---

## 😎 Featured Implementations
- **TypingInChat** ([Modrinth](https://modrinth.com/plugin/typinginchat-plugin)) - Real-time typing visualization

*[Your Project Here 🫵]* - Submit via PR or <a href="https://discord.gg/2UTkYj26B4">Discord</a>!

---

## 👁️ Roadmap & Vision
**2025**  
- Particle-effect holograms
- Interactive holograms
- Improved animation system
- Persistant holograms
- PlaceholderAPI

## Contributors
Contributions to this repo or the example plugin are welcome!

<!-- CONTRIBUTORS:START -->

| Avatar | Username |
|--------|----------|
| [![](https://avatars.githubusercontent.com/u/114857048?v=4&s=50)](https://github.com/max1mde) | [max1mde]( https://github.com/max1mde ) |
| [![](https://avatars.githubusercontent.com/u/116300577?v=4&s=50)](https://github.com/WhyZerVellasskx) | [WhyZerVellasskx]( https://github.com/WhyZerVellasskx ) |

<!-- CONTRIBUTORS:END -->

<div align="center"><sup>Live Statistics</sup></div>

[![img](https://bstats.org/signatures/bukkit/HologramAPI.svg)](https://bstats.org/plugin/bukkit/HologramAPI/19375)

---

<div align="center">
  <sub>Used by 50+ servers | 2.000+ downloads across platforms</sub><br>
  <a href="https://www.spigotmc.org/resources/111746/">SpigotMC</a> •
  <a href="https://hangar.papermc.io/max1mde/HologramLib">Hangar</a> •
  <a href="https://modrinth.com/plugin/hologramlib">Modrinth</a> •
  <a href="https://github.com/max1mde/HologramLib/releases/latest">Latest Release</a> •
  <a href="https://discord.gg/2UTkYj26B4">Support</a><br>
  <sub>License: GPL-3.0 | © 2025 <a href="https://github.com/max1mde/">Maxim</a></sub>
</div>

