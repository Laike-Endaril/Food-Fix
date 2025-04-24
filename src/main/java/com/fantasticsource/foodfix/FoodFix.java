package com.fantasticsource.foodfix;

import com.fantasticsource.mctools.ClientTickTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = FoodFix.MODID, name = FoodFix.NAME, version = FoodFix.VERSION)
public class FoodFix
{
    public static final String MODID = "foodfix";
    public static final String NAME = "Food Fix";
    public static final String VERSION = "1.12.2.001";


    public static boolean checkEating = false;


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FoodFix.class);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientStartUsingItem(LivingEntityUseItemEvent.Start event)
    {
        //Fix bug where the game silently keeps eating food after you stop eating sometimes
        //This is strictly a client-side issue, not involving networking at all, and only occurs if you release right click on a specific tick after eating part of a stack of food
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) livingBase;
            checkEating = true;
            ClientTickTimer.schedule(2, () -> //1 tick was not enough for active itemstack to clear (which might have to do with the root of the issue)
            {
                if (checkEating)
                {
                    checkEating = false;
                    if (player.getActiveItemStack().isEmpty())
                    {
                        Minecraft.getMinecraft().playerController.onStoppedUsingItem(player);
                    }
                }
            });
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientStopUsingItem(LivingEntityUseItemEvent.Stop event)
    {
        //Fix bug where the game silently keeps eating food after you stop eating sometimes (part 2/2)
        //This is strictly a client-side issue, not involving networking at all, and only occurs if you release right click on a specific tick after eating part of a stack of food
        if (event.getEntityLiving() instanceof EntityPlayerSP) checkEating = false;
    }
}
