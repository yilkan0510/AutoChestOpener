package com.github.yilkan0510.chestopener;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class ChestOpenerCommand extends CommandBase {

    private final ChestOpenerMod mod;

    public ChestOpenerCommand(ChestOpenerMod mod) {
        this.mod = mod;
    }

    @Override
    public String getCommandName() {
        return "togglechest";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/togglechest";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        mod.toggleAutoChestOpen();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allows any player to use the command
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // Always allow the command to be executed
    }
}