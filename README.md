## Aurora Core ———— 欧若拉之翼
Aurora Core 是一个库，它提供了一组工具，帮助您构建自己的插件。它始终提供开发人员友好的 API，以使插件之间的交互更加容易。

例如使用下面代码，让玩家的最大生命值随着等级进行改变：

```java
Key<Integer> LEVEL = Key<Double> MAX_HEALTH = Aurora.data().player("minecraft:level", Integer.class);
Key<Double> HEALTH = Aurora.data().player("minecraft:health", Double.class);
Key<Double> MAX_HEALTH = Aurora.data().player("minecraft:max_health", Double.class);

Aurora.of(player)
    .getValue(LEVEL)
    .onChange(event -> {
        event.holder().set(MAX_HEALTH, event.newValue() * 2);
    });
```

```java
ItemStack someItem = Aurora.item()
    .create("neigeitems", "some_item")
    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
```

## 加载顺序
Aurora Core 会自动加载所有的模块，以便于您使用，即使插件没有被启用。

这样，您可以在任何时候使用 AuroraCore，而不必担心加载顺序。

## WIP
- [ ] Data API 中 ValueChangeEvent 的实现 \
      要是和 Sponge 的实现一样，性能会很差（或许自己搓一个event handler?）
- [ ] ItemStack 匹配的实现
- [ ] Action 模型的实现
- [ ] 如何优雅的保存 Entity 的数据

## License
Copyright (c) AnkhOrg <https://inksnow.org/>  All rights reserved

`Data API` 部分参考 Sponge 实现，Copyright (c) SpongePowered <https://www.spongepowered.org>

计划在项目可用时，以 MIT 协议开源，不过目前尚不确定具体实现