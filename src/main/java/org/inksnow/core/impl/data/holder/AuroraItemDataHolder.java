package org.inksnow.core.impl.data.holder;

import org.bukkit.inventory.ItemStack;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.ItemDataHolder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.persistence.NBTTranslator;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.nbt.AuroraTagFactory;
import org.inksnow.core.impl.ref.nbt.RefCraftItemStack;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.nbt.CompoundTag;

import java.util.Collections;
import java.util.List;

public final class AuroraItemDataHolder implements ItemDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final RefCraftItemStack itemStack;

    public AuroraItemDataHolder(ItemStack itemStack) {
        if (itemStack instanceof RefCraftItemStack) {
            this.itemStack = (RefCraftItemStack) itemStack;
        } else {
            throw new IllegalArgumentException("ItemStack must be a CraftItemStack");
        }
    }

    // not necessary, for performance
    @Override
    public List<DataHolder.Mutable> impl$mutableDelegateDataHolder() {
        return Collections.singletonList(this);
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.ITEMSTACK;
    }

    @Override
    public void setDataContainer(DataContainer container) {
        RefNbtTagCompound nbt = (RefNbtTagCompound) AuroraTagFactory.INSTANCE.unwrap(
                NBTTranslator.INSTANCE.translate(container)
        );
        itemStack.handle.setTag(nbt);
    }

    @Override
    public DataContainer getDataContainer() {
        RefNbtTagCompound nbt = itemStack.handle.getTag();
        return NBTTranslator.INSTANCE.translate((CompoundTag) AuroraTagFactory.INSTANCE.wrap(nbt));
    }
}
