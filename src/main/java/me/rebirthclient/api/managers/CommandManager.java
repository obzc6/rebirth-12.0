/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 */
package me.rebirthclient.api.managers;

import java.lang.reflect.Field;
import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.interfaces.IChatHud;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.commands.Command;
import me.rebirthclient.mod.commands.impl.AimCommand;
import me.rebirthclient.mod.commands.impl.BindCommand;
import me.rebirthclient.mod.commands.impl.FriendCommand;
import me.rebirthclient.mod.commands.impl.PrefixCommand;
import me.rebirthclient.mod.commands.impl.ReloadCommand;
import me.rebirthclient.mod.commands.impl.SaveCommand;
import me.rebirthclient.mod.commands.impl.TeleportCommand;
import me.rebirthclient.mod.commands.impl.ToggleCommand;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.text.Text;

public class CommandManager
implements Wrapper {
    public static String syncCode = "\u00a7(";
    private final HashMap<String, Command> commands = new HashMap();
    public final AimCommand aim = new AimCommand();
    public final TeleportCommand tp = new TeleportCommand();
    public final BindCommand bind = new BindCommand();
    public final ToggleCommand toggle = new ToggleCommand();
    public final PrefixCommand prefix = new PrefixCommand();
    public final FriendCommand friend = new FriendCommand();
    public final ReloadCommand reload = new ReloadCommand();
    public final SaveCommand save = new SaveCommand();

    public CommandManager() {
        try {
            for (Field field : CommandManager.class.getDeclaredFields()) {
                if (!Command.class.isAssignableFrom(field.getType())) continue;
                Command cmd = (Command)field.get(this);
                this.commands.put(cmd.getName(), cmd);
            }
        }
        catch (Exception e) {
            System.out.println("Error initializing Rebirth commands.");
            System.out.println(e.getStackTrace().toString());
        }
    }

    public Command getCommandBySyntax(String string) {
        return this.commands.get(string);
    }

    public HashMap<String, Command> getCommands() {
        return this.commands;
    }

    public int getNumOfCommands() {
        return this.commands.size();
    }

    public void command(String[] commandIn) {
        Command command = this.commands.get(commandIn[0].substring(Rebirth.PREFIX.length()));
        if (command == null) {
            CommandManager.sendChatMessage("\u00a7c[!] \u00a7fInvalid Command! Type \u00a7ehelp \u00a7ffor a list of commands.");
        } else {
            String[] parameterList = new String[commandIn.length - 1];
            for (int i = 1; i < commandIn.length; ++i) {
                parameterList[i - 1] = commandIn[i];
            }
            if (parameterList.length == 1 && parameterList[0].equals("help")) {
                command.sendUsage();
                return;
            }
            command.runCommand(parameterList);
        }
    }

    public static void sendChatMessage(String message) {
        if (Module.nullCheck() || !Rebirth.loaded) {
            return;
        }
        CommandManager.mc.inGameHud.getChatHud().addMessage(Text.of((String)(syncCode + "\u00a7r[" + Rebirth.getName() + "]\u00a7f " + message)));
    }

    public static void sendChatMessageWidthId(String message, int id) {
        if (Module.nullCheck() || !Rebirth.loaded) {
            return;
        }
        ((IChatHud)CommandManager.mc.inGameHud.getChatHud()).rebirth_nextgen_master$add(Text.of((String)(syncCode + "\u00a7r[" + Rebirth.getName() + "]\u00a7f " + message)), id);
    }
}

