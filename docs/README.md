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
