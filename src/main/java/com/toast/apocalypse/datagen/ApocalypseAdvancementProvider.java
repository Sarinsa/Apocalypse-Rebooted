package com.toast.apocalypse.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class ApocalypseAdvancementProvider extends AdvancementProvider {

    private final DataGenerator dataGenerator;
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ImmutableList<Consumer<Consumer<Advancement>>> advancements;

    @SafeVarargs
    public ApocalypseAdvancementProvider(DataGenerator dataGenerator, Consumer<Consumer<Advancement>>... consumers) {
        super(dataGenerator);
        this.dataGenerator = dataGenerator;
        this.advancements = ImmutableList.copyOf(consumers);
    }

    @Override
    public void run(DirectoryCache cache) {
        Path path = this.dataGenerator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();

        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            else {
                Path path1 = getPath(path, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                } catch (IOException ioexception) {
                    Apocalypse.LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };
        for(Consumer<Consumer<Advancement>> consumer1 : this.advancements) {
            consumer1.accept(consumer);
        }
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + Apocalypse.MODID + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }
}
