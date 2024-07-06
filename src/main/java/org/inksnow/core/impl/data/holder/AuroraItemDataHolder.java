package org.inksnow.core.impl.data.holder;

import org.bukkit.inventory.ItemStack;
import org.inksnow.core.data.holder.ItemDataHolder;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.provider.nbt.NBTDataType;
import org.inksnow.core.impl.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefCraftItemStack;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

public final class AuroraItemDataHolder implements ItemDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final RefCraftItemStack itemStack;

    public AuroraItemDataHolder(ItemStack itemStack) {
        if (itemStack instanceof RefCraftItemStack) {
            this.itemStack = (RefCraftItemStack) itemStack;
        } else {
            throw new IllegalArgumentException("ItemStack must be a CraftItemStack");
        }
    }

    @Override
    public RefNbtTagCompound data$getCompound() {
        return itemStack.handle.getTag();
    }

    @Override
    public void data$setCompound(RefNbtTagCompound nbt) {
        itemStack.handle.setTag(nbt);
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.ITEMSTACK;
    }
}
