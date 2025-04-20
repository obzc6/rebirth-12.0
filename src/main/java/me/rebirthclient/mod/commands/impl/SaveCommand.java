/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.commands.impl;

import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.commands.Command;

public class SaveCommand
extends Command {
    public SaveCommand() {
        super("save", "save", "");
    }

    @Override
    public void runCommand(String[] parameters) {
        CommandManager.sendChatMessage("\u00a7e[!] \u00a7fSaving..");
        Rebirth.save();
    }

    @Override
    public String[] getAutocorrect(int count, List<String> seperated) {
        return null;
    }
}

