package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.ACID_RAIN;

// TODO - DON'T FORGET!!!!! Rain damage is currently only applied to entities in the overworld!
@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RainDamageTickHandler {

    private static boolean acidSnowEnabled = false;
    private static int timeRainDmgCheck;


    private RainDamageTickHandler() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        acidSnowEnabled = ACID_RAIN.GENERAL.acidSnow.get();
    }

    /**
     * Checks if it is time to apply acid rain tick damage,
     * and applies damage to all exposed living entities (or only players depending on config).
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        boolean isRainingAcid = Apocalypse.INSTANCE.getDifficultyManager().isRainingAcid(event.getServer().overworld());

        if (!isRainingAcid || ACID_RAIN.GENERAL.rainDamage.get() <= 0)
            return;

        if (++timeRainDmgCheck >= (ACID_RAIN.GENERAL.damageRate.get() * 20)) {
            ServerLevel overworld = event.getServer().overworld();
            boolean playersOnly = !ACID_RAIN.GENERAL.damageMobs.get();

            if (playersOnly) {
                for (ServerPlayer player : overworld.players()) {
                    if (player.level().dimension() != Level.OVERWORLD) continue;

                    boolean rainingAcidAt = acidSnowEnabled
                            ? isRainingOrSnowingAt(overworld, player.blockPosition().offset(0, (int) player.getEyeHeight(), 0))
                            : overworld.isRainingAt(player.blockPosition().offset(0, (int) player.getEyeHeight(), 0));

                    if (EnchantmentHelper.hasAquaAffinity(player) || !rainingAcidAt)
                        continue;

                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);

                    if (!headStack.isEmpty()) {
                        if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                            continue;
                        }
                        headStack.hurtAndBreak(player.getRandom().nextInt(2), player, (playerEntity) -> player.broadcastBreakEvent(EquipmentSlot.HEAD));
                    }
                    else {
                        player.hurt(ApocalypseDamageSources.of(overworld, ApocalypseDamageSources.ACID_RAIN), ACID_RAIN.GENERAL.rainDamage.get());
                    }
                }
            }
            else {
                Iterable<Entity> allEntities = overworld.getAllEntities();

                for (Entity entity : allEntities) {
                    if (entity.level().dimension() != Level.OVERWORLD || ACID_RAIN.GENERAL.mobBlacklist.contains(entity)) continue;

                    if (entity instanceof LivingEntity livingEntity) {
                        boolean rainingAcidAt = acidSnowEnabled
                                ? isRainingOrSnowingAt(overworld, livingEntity.blockPosition().offset(0, (int) livingEntity.getEyeHeight(), 0))
                                : overworld.isRainingAt(livingEntity.blockPosition().offset(0, (int) livingEntity.getEyeHeight(), 0));

                        if (EnchantmentHelper.hasAquaAffinity(livingEntity) || !rainingAcidAt)
                            continue;

                        ItemStack headStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);

                        if (!headStack.isEmpty()) {
                            if (headStack.getItem() == ApocalypseItems.BUCKET_HELM.get() || headStack.getItem().getMaxDamage(headStack) <= 0) {
                                continue;
                            }
                            headStack.hurtAndBreak(livingEntity.getRandom().nextInt(2), livingEntity, (playerEntity) -> livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD));
                        }
                        else {
                            livingEntity.hurt(ApocalypseDamageSources.of(overworld, ApocalypseDamageSources.ACID_RAIN), ACID_RAIN.GENERAL.rainDamage.get());
                        }
                    }
                }
            }
            resetTimer();
        }
    }

    /**
     * @return True if it is currently raining or snowing at the given block position.
     */
    public static boolean isRainingOrSnowingAt(Level level, BlockPos pos) {
        if (!level.isRaining()) {
            return false;
        }
        else if (!level.canSeeSky(pos)) {
            return false;
        }
        else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        else {
            Biome biome = level.getBiome(pos).value();
            return biome.getPrecipitationAt(pos) == Biome.Precipitation.RAIN || biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW;
        }
    }

    /**
     * Call to reset damage tick timer.
     */
    public static void resetTimer() {
        timeRainDmgCheck = 0;
    }
}
