package com.snackpirate.joy_of_tinkering.items;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.item.ModifiableItemClientExtension;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static com.snackpirate.joy_of_tinkering.items.ModifiableGunItem.GUN_AMMO;

//inspired by Iron's Spellbooks
public class GunArmPose extends ModifiableItemClientExtension {
    public static final GunArmPose INSTANCE = new GunArmPose();
    @OnlyIn(Dist.CLIENT)
    public static HumanoidModel.ArmPose ONE_HANDED = HumanoidModel.ArmPose.create("TINKER_ADDITIONS_ONE_HANDED", false, (model, entity, arm) -> {
        ModelPart correctArm = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        correctArm.xRot =
                model.head.xRot - (float)(Math.PI / 2);
//                Mth.lerp(.85f, correctArm.xRot,
//                        ((-(float) Math.PI / 3.5F) + model.head.xRot / 2f));
        correctArm.yRot = model.head.yRot;
    });

    @OnlyIn(Dist.CLIENT)
    public static HumanoidModel.ArmPose TWO_HANDED = HumanoidModel.ArmPose.create("TINKER_ADDITIONS_TWO_HANDED", true, (model, entity, arm) -> {
        AnimationUtils.animateCrossbowHold(model.rightArm, model.leftArm, model.head, arm == HumanoidArm.RIGHT);
        ModelPart correctArm = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        correctArm.xRot =
                model.head.xRot - (float)(Math.PI / 2);
//                Mth.lerp(.85f, correctArm.xRot,
//                        ((-(float) Math.PI / 3.5F) + model.head.xRot / 2f));
        correctArm.yRot = model.head.yRot;
    });

    @Nullable
    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity living, InteractionHand hand, ItemStack stack) {
        ModDataNBT persistentData = ToolStack.from(stack).getPersistentData();
        ListTag heldAmmo = (ListTag) persistentData.get(GUN_AMMO);
        // ignore warning, null check is necessary
        if (heldAmmo != null && !heldAmmo.isEmpty()) {
            return stack.is(TinkerTags.Items.BROAD_RANGED) ? TWO_HANDED : ONE_HANDED;
        }
        return HumanoidModel.ArmPose.ITEM;
    }
}
