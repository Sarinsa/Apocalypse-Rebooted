package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CommonMixinHooks {

    /**
     * Item entities die instantly when struck by lightning bolts,
     * so we need to make an exception for the glorious fatherly toast.
     */
    public static void itemEntityOnHurt(DamageSource damageSource, float damage, ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (damageSource == DamageSource.LIGHTNING_BOLT && itemStack.getItem() == ApocalypseItems.FATHERLY_TOAST.get())
            callbackInfo.setReturnValue(false);
    }
}
