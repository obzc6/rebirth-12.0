/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.Session
 *  net.minecraft.client.util.Session$AccountType
 *  org.apache.commons.io.IOUtils
 */
package me.rebirthclient.api.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.rebirthclient.api.alts.Alt;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IMinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.io.IOUtils;

public class AltManager
implements Wrapper {
    private final ArrayList<Alt> alts = new ArrayList();

    public AltManager() {
        this.readAlts();
    }

    public void readAlts() {
        try {
            File altFile = new File(AltManager.mc.runDirectory, "rebirth_alts.txt");
            if (!altFile.exists()) {
                throw new IOException("File not found! Could not load alts...");
            }
            List list = IOUtils.readLines((InputStream)new FileInputStream(altFile), (Charset)StandardCharsets.UTF_8);
            for (Object s : list) {
                this.alts.add(new Alt((String) s));
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveAlts() {
        PrintWriter printwriter = null;
        try {
            File altFile = new File(AltManager.mc.runDirectory, "rebirth_alts.txt");
            System.out.println("[Rebirth] Saving Alts");
            printwriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(altFile), StandardCharsets.UTF_8));
            for (Alt alt : this.alts) {
                printwriter.println(alt.getEmail());
            }
        }
        catch (Exception exception) {
            System.out.println("[Rebirth] Failed to save alts");
        }
        printwriter.close();
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
    }

    public void removeAlt(Alt alt) {
        this.alts.remove(alt);
    }

    public ArrayList<Alt> getAlts() {
        return this.alts;
    }

    public void loginCracked(String alt) {
        try {
            ((IMinecraftClient)mc).setSession(new Session(alt, "", "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginToken(String name, String token, String uuid) {
        try {
            ((IMinecraftClient)mc).setSession(new Session(name, uuid, token, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

