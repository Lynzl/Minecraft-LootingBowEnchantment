package fr.lnzl.lbe;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(LootingBowEnchantment.MODID)
@Mod.EventBusSubscriber
public class LootingBowEnchantment {

    public static final String MODID = "lootingbowenchantment";

    public static final DeferredRegister<Enchantment> ENCHANTMENTS_REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    public static final RegistryObject<Enchantment> LOOTING_ENCHANTMENT = ENCHANTMENTS_REGISTRY.register("looting", LootingEnchantment::new);

    private static final Logger LOGGER = LogManager.getLogger(MODID);


    public LootingBowEnchantment() {

        ENCHANTMENTS_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLootingLevelEvent(final LootingLevelEvent event) {
        int lootingLevel = event.getLootingLevel();

        Entity entity = event.getDamageSource().getTrueSource();
        if (entity == null) return;

        Iterable<ItemStack> heldEquipment = entity.getHeldEquipment();
        for (ItemStack equipped : heldEquipment) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(equipped);
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                if (enchantment.getKey() instanceof LootingEnchantment) {
                    if (enchantment.getValue() > lootingLevel)
                        lootingLevel = enchantment.getValue();
                }
            }
        }
        event.setLootingLevel(lootingLevel);
    }


    public static class LootingEnchantment extends Enchantment {

        protected LootingEnchantment() {
            super(
                    Rarity.UNCOMMON,
                    EnchantmentType.create(
                            "ALLBOWS",
                            (item) -> (item instanceof BowItem || item instanceof CrossbowItem)
                    ),
                    new EquipmentSlotType[]{
                            EquipmentSlotType.MAINHAND,
                            EquipmentSlotType.OFFHAND
                    }
            );
        }

        public int getMinEnchantability(int enchantmentLevel) {
            return 15 + (enchantmentLevel - 1) * 9;
        }

        public int getMaxEnchantability(int enchantmentLevel) {
            return super.getMinEnchantability(enchantmentLevel) + 50;
        }

        public int getMaxLevel() {
            return 3;
        }

        public boolean canApplyTogether(Enchantment ench) {
            return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH;
        }
    }
}
