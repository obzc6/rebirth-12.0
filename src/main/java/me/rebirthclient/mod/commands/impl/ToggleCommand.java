/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.commands.impl;

import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.commands.Command;
import me.rebirthclient.mod.modules.Module;

public class ToggleCommand
extends Command {
    public ToggleCommand() {
        super("toggle", "Toggle module", "[module]");
    }

    @Override
    public void runCommand(String[] parameters) {
        if (parameters.length == 0) {
            this.sendUsage();
            return;
        }
        String moduleName = parameters[0];
        Module module = Rebirth.MODULE.getModuleByName(moduleName);
        if (module == null) {
            CommandManager.sendChatMessage("\u00a74[!] \u00a7fUnknown \u00a7bmodule!");
            return;
        }
        module.toggle();
    }

    @Override
    public String[] getAutocorrect(int count, List<String> seperated) {
        if (count == 1) {
            String input = seperated.get(seperated.size() - 1).toLowerCase();
            ModuleManager cm = Rebirth.MODULE;
            ArrayList<String> correct = new ArrayList<String>();
            for (Module x : cm.modules) {
                if (!input.equalsIgnoreCase(Rebirth.PREFIX + "toggle") && !x.getName().toLowerCase().startsWith(input)) continue;
                correct.add(x.getName());
            }
            int numCmds = correct.size();
            String[] commands = new String[numCmds];
            int i = 0;
            for (String x : correct) {
                commands[i++] = x;
            }
            return commands;
        }
        return null;
    }
}

