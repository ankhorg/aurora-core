package org.inksnow.core.impl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataManager;
import org.inksnow.core.data.holder.DataHolderBuilder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.AbstractDataBuilder;
import org.inksnow.core.data.persistence.DataBuilder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataSerializable;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.KeyBasedDataListener;
import org.inksnow.core.impl.data.provider.DataProviderRegistry;
import org.inksnow.core.impl.data.store.DataStoreRegistry;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Singleton
public class AuroraDataManager implements DataManager {

    public static AuroraDataManager INSTANCE;

    private final DataStoreRegistry dataStoreRegistry;
    private final DataProviderRegistry dataProviderRegistry;
    private final Map<Class<?>, DataBuilder<?>> builders;
    private final Map<Class<? extends DataHolder.Immutable<?>>, DataHolderBuilder.Immutable<?, ?>> immutableDataBuilderMap;
    private final Map<String, ResourcePath> legacyRegistrations;
    private final List<KeyBasedDataListener<?>> keyListeners;
    private final Map<String, DataQuery> legacySpongeData = new HashMap<>();

    @com.google.inject.Inject
    private AuroraDataManager() {
        AuroraDataManager.INSTANCE = this;

        this.dataStoreRegistry = new DataStoreRegistry();
        this.dataProviderRegistry = new DataProviderRegistry();
        this.builders = new HashMap<>();
        this.immutableDataBuilderMap = new MapMaker()
                .concurrencyLevel(4)
                .makeMap();
        this.legacyRegistrations = new HashMap<>();
        this.keyListeners = new ArrayList<>();
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(final Class<T> clazz, final DataBuilder<T> builder) {
        Objects.requireNonNull(builder);

        if (this.builders.putIfAbsent(clazz, builder) != null) {
            logger.warn("A DataBuilder has already been registered for {}. Attempted to register {} instead.", clazz,
                    builder.getClass());
        } else if (!(builder instanceof AbstractDataBuilder)) {
            logger.warn("A custom DataBuilder is not extending AbstractDataBuilder! It is recommended that "
                    + "the custom data builder does extend it to gain automated content versioning updates and maintain "
                    + "simplicity. The offending builder's class is: {}", builder.getClass());
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends DataSerializable> Optional<DataBuilder<T>> builder(final Class<T> clazz) {
        final DataBuilder<?> dataBuilder = this.builders.get(clazz);
        if (dataBuilder != null) {
            return Optional.of((DataBuilder<T>) dataBuilder);
        }
        return Optional.ofNullable((DataBuilder<T>) this.immutableDataBuilderMap.get(clazz));
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(final Class<T> clazz, final DataView dataView) {
        Objects.requireNonNull(dataView);

        return this.builder(clazz).flatMap(builder -> builder.build(dataView));
    }

    @Override
    public <T extends DataHolder.Immutable<T>, B extends DataHolderBuilder.Immutable<T, B>> void register(final Class<T> holderClass, final B builder) {
        Objects.requireNonNull(builder);

        final DataHolderBuilder.Immutable<?, ?> previous = this.immutableDataBuilderMap.putIfAbsent(holderClass, builder);
        if (previous != null) {
            throw new IllegalStateException("Already registered the DataUtil for " + holderClass.getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataHolder.Immutable<T>, B extends DataHolderBuilder.Immutable<T, B>> Optional<B> immutableBuilder(final Class<T> holderClass) {
        return Optional.ofNullable((B) this.immutableDataBuilderMap.get(Objects.requireNonNull(holderClass)));
    }

    @Override
    public <T> Optional<DataTranslator<T>> translator(final Class<T> objectClass) {
        return DataTranslatorProvider.INSTANCE.getSerializer(objectClass);
    }

    @Override
    public <T> void registerTranslator(final Class<T> objectClass, final DataTranslator<T> translator) {
        DataTranslatorProvider.INSTANCE.register(objectClass, translator);
    }

    /* public void registerKeyListeners() {
        this.keyListeners.forEach(this::registerKeyListener0);
        this.keyListeners.clear();
    }

    private void registerKeyListener0(final KeyBasedDataListener<?> listener) {
        Sponge.eventManager().registerListener(EventListenerRegistration.builder(ChangeDataHolderEvent.ValueChange.class)
                .plugin(listener.getOwner())
                .listener(listener)
                .build()
        );
    } */

    @Override
    public void registerLegacyManipulatorIds(final String legacyId, final ResourcePath dataStoreKey) {
        Objects.requireNonNull(legacyId);
        Objects.requireNonNull(dataStoreKey);

        final ResourcePath previous = this.legacyRegistrations.putIfAbsent(legacyId, dataStoreKey);
        if (previous != null) {
            throw new IllegalStateException("Legacy registration id already registered: id" + legacyId + " for registration: " + dataStoreKey);
        }
    }

    public Optional<ResourcePath> getLegacyRegistration(final String id) {
        return Optional.ofNullable(this.legacyRegistrations.get(id));
    }

    @Override
    public DataContainer createContainer() {
        return new MemoryDataContainer();
    }

    @Override
    public DataContainer createContainer(final DataView.SafetyMode safety) {
        return new MemoryDataContainer(safety);
    }

    public <E extends DataHolder> void registerKeyListener(final KeyBasedDataListener<E> keyListener) {
        Objects.requireNonNull(keyListener);

        this.keyListeners.add(keyListener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerCustomDataRegistration(final AuroraDataRegistration registration) {
        for (final DataStore dataStore : registration.getDataStores()) {
            this.dataStoreRegistry.register(dataStore, registration.keys());
        }

        for (final Key key : registration.keys()) {
            this.registerCustomDataProviderForKey(registration, key);
        }
    }

    private <V extends Value<E>, E> void registerCustomDataProviderForKey(final AuroraDataRegistration registration, final Key<V> key) {
        final Collection<DataProvider<V, E>> providers = registration.providersFor(key);

        final Set<Type> dataStoreSupportedTokens = new HashSet<>();
        this.dataStoreRegistry.getDataStores(key).stream().map(DataStore::supportedTypes).forEach(dataStoreSupportedTokens::addAll);

        for (final DataProvider<V, E> provider : providers) {
            this.dataProviderRegistry.register(provider);
            dataStoreSupportedTokens.removeIf(provider::isSupported);
        }

        // For all tokens supported by a datastore register a CustomDataProvider
        if (!dataStoreSupportedTokens.isEmpty()) {
            this.dataProviderRegistry.register(new CustomDataProvider<>(key, dataStoreSupportedTokens));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerDataRegistration(final AuroraDataRegistration registration) {
        for (final DataStore dataStore : registration.getDataStores()) {
            this.dataStoreRegistry.register(dataStore, registration.keys());
        }
        for (final Key key : registration.keys()) {
            final Collection<DataProvider<?, ?>> providers = registration.providersFor(key);
            for (DataProvider<?, ?> provider : providers) {
                this.dataProviderRegistry.register(provider);
            }
        }
    }

    /* public void registerDefaultProviders() {
        this.dataProviderRegistry.registerDefaultProviders();
        new EntityAirSupplyConverter();
        new EntityBabyConverter();
        new EntityCustomNameConverter();
        new EntityCustomNameVisibleConverter();
        new EntityFlagsConverter();
        new EntityNoGravityConverter();
        new EntitySilentConverter();
        new LivingEntityArrowCountConverter();
        new LivingHealthConverter();
        new MobEntityAIFlagsConverter();

    } */

    public static DataStoreRegistry getDatastoreRegistry() {
        return AuroraDataManager.INSTANCE.dataStoreRegistry;
    }

    public static DataProviderRegistry getProviderRegistry() {
        return AuroraDataManager.INSTANCE.dataProviderRegistry;
    }

    public void registerDefaultBuilders() {
        /* this.registerBuilder(ItemStack.class, new SpongeItemStack.BuilderImpl());
        this.registerBuilder(ItemStackSnapshot.class, new SpongeItemStackSnapshotDataBuilder());
        this.registerBuilder(EntitySnapshot.class, new SpongeEntitySnapshotBuilder());
        this.registerBuilder(EntityArchetype.class, SpongeEntityArchetypeBuilder.unpooled());
        this.registerBuilder(SpongePlayerData.class, new SpongePlayerDataBuilder());
        this.registerBuilder(BlockState.class, new SpongeBlockStateBuilder());
        this.registerBuilder(MapDecoration.class, new SpongeMapDecorationDataBuilder());
        this.registerBuilder(MapCanvas.class, new SpongeMapCanvasDataBuilder());
        this.registerBuilder(ServerLocation.class, new SpongeServerLocationBuilder());
        this.registerBuilder(GameProfile.class, new SpongeGameProfileDataBuilder());
        this.registerBuilder(ProfileProperty.class, new SpongeProfilePropertyDataBuilder());
        this.registerBuilder(Color.class, new Color.Builder());
        this.registerBuilder(RespawnLocation.class, new RespawnLocation.Builder());
        this.registerBuilder(BlockSnapshot.class, SpongeBlockSnapshot.BuilderImpl.unpooled());
        this.registerBuilder(BlockEntityArchetype.class, SpongeBlockEntityArchetypeBuilder.unpooled());
        this.registerBuilder(FireworkEffect.class, new SpongeFireworkEffectDataBuilder());
        this.registerBuilder(BannerPatternLayer.class, new SpongePatternLayerBuilder());
        this.registerBuilder(VariableAmount.BaseAndAddition.class, new BaseAndAdditionBuilder());
        this.registerBuilder(VariableAmount.BaseAndVariance.class, new BaseAndVarianceBuilder());
        this.registerBuilder(VariableAmount.Fixed.class, new FixedBuilder());
        this.registerBuilder(VariableAmount.OptionalAmount.class, new OptionalVarianceBuilder());
        this.registerBuilder(ParticleEffect.class, new SpongeParticleEffectBuilder());
        this.registerBuilder(PotionEffect.class, new SpongePotionBuilder());
        this.registerBuilder(FluidStack.class, new SpongeFluidStackBuilder());
        this.registerBuilder(FluidStackSnapshot.class, new SpongeFluidStackSnapshotBuilder());
        this.registerBuilder(Enchantment.class, new SpongeEnchantmentBuilder());
        this.registerBuilder(TradeOffer.class, new SpongeTradeOfferBuilder());
        this.registerBuilder(LocatableBlock.class, new SpongeLocatableBlockBuilder()); */
    }

    public Optional<DataStore> getDataStore(ResourcePath key, Class<? extends DataHolder> typeToken) {
        return this.dataStoreRegistry.getDataStore(key, typeToken);
    }

    public @Nullable DataQuery legacySpongeDataQuery(String nbtKey) {
        return this.legacySpongeData.get(nbtKey);
    }
}
