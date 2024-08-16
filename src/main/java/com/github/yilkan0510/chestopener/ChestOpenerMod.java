package com.github.yilkan0510.chestopener;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;

import java.awt.Robot;
import java.awt.event.InputEvent;

@Mod(modid = ChestOpenerMod.MODID, version = ChestOpenerMod.VERSION)
public class ChestOpenerMod {

    public static final String MODID = "chestopener";
    public static final String VERSION = "1.0";
    private boolean autoChestOpenEnabled = false;
    private boolean cooldownChest = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    private Robot robot;

    public ChestOpenerMod() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register the event bus to handle custom events
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        // Register the /togglechest command
        event.registerServerCommand(new ChestOpenerCommand(this));
    }

    public void toggleAutoChestOpen() {
        autoChestOpenEnabled = !autoChestOpenEnabled;
        String status = autoChestOpenEnabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
        String message = EnumChatFormatting.AQUA + "Auto Chest Open: " + status;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (autoChestOpenEnabled) {
            // Check what the player is looking at
            MovingObjectPosition rayTraceResult = mc.objectMouseOver;

            if (rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectType.BLOCK) {
                BlockPos blockPos = rayTraceResult.getBlockPos();

                if (mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.chest) {
                    cooldownChest = false;
                }

                if (!cooldownChest) {
                    if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest) {
                        // Simulate right-click action using Robot class
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        mc.thePlayer.addChatMessage(new ChatComponentText("chest opened"));
                        cooldownChest = true;
                    }
                }
            } else {
                // Reset cooldown if looking at air or other non-block objects
                cooldownChest = false;
            }
        }
    }
}