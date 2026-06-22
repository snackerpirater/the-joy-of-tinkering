package com.snackpirate.joy_of_tinkering.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class SlimeBucketItem extends Item {

	private final Supplier<? extends EntityType<?>> entityTypeSupplier;
	public SlimeBucketItem(Properties pProperties, Supplier<? extends EntityType<?>> entityTypeSupplier) {
		super(pProperties);
		this.entityTypeSupplier = entityTypeSupplier;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
		InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(pPlayer, pLevel, itemstack, blockhitresult);
		if (ret != null) return ret;
		if (blockhitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			BlockPos blockpos = blockhitresult.getBlockPos();
			Direction direction = blockhitresult.getDirection();
			BlockPos blockpos1 = blockpos.relative(direction);
			if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos1, direction, itemstack)) {
				if (pLevel instanceof ServerLevel server) {
					Entity entity = entityTypeSupplier.get().create(server);
					entity.moveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D, Mth.wrapDegrees(pLevel.random.nextFloat() * 360.0F), 0.0F);
					if (entity instanceof Slime slime) {
						slime.setSize(1, true);
						slime.yHeadRot = slime.getYRot();
						slime.yBodyRot = slime.getYRot();
						slime.playAmbientSound();
						pLevel.addFreshEntity(slime);
					}
				}
				pLevel.playSound(pPlayer, blockpos1, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				if (!pPlayer.getAbilities().instabuild) pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
				return InteractionResultHolder.success(itemstack);
			}
		}
		return super.use(pLevel, pPlayer, pHand);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(Component.translatable("item.joy_of_tinkering.slime_bucket.tooltip", Component.translatable(entityTypeSupplier.get().getDescriptionId())).withStyle(ChatFormatting.GRAY));
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
}
