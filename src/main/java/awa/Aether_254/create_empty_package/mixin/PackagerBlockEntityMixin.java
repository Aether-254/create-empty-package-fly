package awa.Aether_254.create_empty_package.mixin;

import com.zurrtum.create.content.logistics.box.PackageItem;
import com.zurrtum.create.content.logistics.packager.PackagerBlockEntity;
import com.zurrtum.create.content.logistics.packager.PackagerItemHandler;
import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackagerBlockEntity.class)
abstract class PackagerBlockEntityMixin {
    @Inject(method = "attemptToSend()V", at = @At("HEAD"), cancellable = true)
    private void createEmptyPackage$packageEmptyTarget(CallbackInfo ci) {
        PackagerBlockEntity packager = (PackagerBlockEntity) (Object) this;
        if (!packager.heldBox.isEmpty() || packager.animationTicks != 0 || packager.buttonCooldown > 0)
            return;

        Container target = packager.targetInventory.getInventory();
        if (target instanceof PackagerItemHandler || target != null && !target.isEmpty())
            return;

        ItemStack box = PackageItem.containing(new ItemStackHandler(PackageItem.SLOTS));
        if (!packager.signBasedAddress.isBlank())
            PackageItem.addAddress(box, packager.signBasedAddress);

        packager.heldBox = box;
        packager.animationInward = false;
        packager.animationTicks = PackagerBlockEntity.CYCLE;
        packager.triggerStockCheck();
        packager.notifyUpdate();
        ci.cancel();
    }
}
