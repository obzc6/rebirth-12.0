/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.commands.impl;

import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.commands.Command;

public class PrefixCommand
extends Command {
    public PrefixCommand() {
        super("prefix", "Set prefix", "[prefix]");
    }

    @Override
    public void runCommand(String[] parameters) {
        if (parameters.length == 0) {
            this.sendUsage();
            return;
        }
        if (parameters[0].startsWith("/")) {
            CommandManager.sendChatMessage("\u00a76[!] \u00a7fPlease specify a valid \u00a7bprefix.");
            return;
        }
        Rebirth.PREFIX = parameters[0];
        CommandManager.sendChatMessage("\u00a7a[\u221a] \u00a7bPrefix \u00a7fset to \u00a7e" + parameters[0]);
    }

    @Override
    public String[] getAutocorrect(int count, List<String> seperated) {
        return null;
    }
}

