package com.github.yilkan0510.chestopener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = ChestOpenerMod.MODID, version = ChestOpenerMod.VERSION)
public class ChestOpenerMod {

    public static final String MODID = "chestopener";
    public static final String VERSION = "1.1";
    private static final String KEY_CATEGORY = "Auto Chest Opener";
    private boolean autoChestOpenEnabled = false;
    private boolean cooldownChest = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    private Robot robot;
    private KeyBinding toggleChestKey;

    public ChestOpenerMod() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        toggleChestKey = new KeyBinding("key.togglechest", Keyboard.KEY_C, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(toggleChestKey);
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
        if (toggleChestKey.isPressed()) {
            toggleAutoChestOpen();
        }

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
                        // Release left click if pressed
                        if (Mouse.isButtonDown(0)) {
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            // Use a timer to introduce a delay
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // Simulate right-click action using Robot class
                                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                                }
                            }, 50); // 100ms delay
                        } else {
                            // Simulate right-click action using Robot class
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        }
                        cooldownChest = true;
                    }
                }
            } else {
                // Reset cooldown if looking at air or other non-block objects
                cooldownChest = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.startsWith("/togglechest")) {
            event.setCanceled(true);
            toggleAutoChestOpen();
        }
    }
}