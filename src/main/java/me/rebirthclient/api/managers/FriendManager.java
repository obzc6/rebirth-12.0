/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.entity.player.PlayerEntity
 *  org.apache.commons.io.IOUtils
 */
package me.rebirthclient.api.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.rebirthclient.api.util.Wrapper;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.io.IOUtils;

public class FriendManager
implements Wrapper {
    public ArrayList<String> friendList = new ArrayList();

    public FriendManager() {
        this.readFriends();
    }

    public boolean isFriend(String name) {
        return this.friendList.contains(name);
    }

    public void removeFriend(String name) {
        this.friendList.remove(name);
    }

    public void addFriend(String name) {
        if (!this.friendList.contains(name)) {
            this.friendList.add(name);
        }
    }

    public void friend(String name) {
        if (this.friendList.contains(name)) {
            this.friendList.remove(name);
        } else {
            this.friendList.add(name);
        }
    }

    public void readFriends() {
        try {
            File friendFile = new File(FriendManager.mc.runDirectory, "rebirth_friends.txt");
            if (!friendFile.exists()) {
                throw new IOException("File not found! Could not load friends...");
            }
            List list = IOUtils.readLines((InputStream)new FileInputStream(friendFile), (Charset)StandardCharsets.UTF_8);
            for (Object s : list) {
                this.addFriend((String) s);
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveFriends() {
        PrintWriter printwriter = null;
        try {
            File friendFile = new File(FriendManager.mc.runDirectory, "rebirth_friends.txt");
            System.out.println("[Rebirth] Saving Friends");
            printwriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(friendFile), StandardCharsets.UTF_8));
            for (String str : this.friendList) {
                printwriter.println(str);
            }
        }
        catch (Exception exception) {
            System.out.println("[Rebirth] Failed to save friends");
        }
        printwriter.close();
    }

    public void loadFriends() throws IOException {
        String modName = "rebirth_friends.json";
        Path modPath = Paths.get(modName, new String[0]);
        if (!Files.exists(modPath, new LinkOption[0])) {
            return;
        }
        this.loadPath(modPath);
    }

    private void loadPath(Path path) throws IOException {
        InputStream stream = Files.newInputStream(path, new OpenOption[0]);
        try {
            this.loadFile(new JsonParser().parse((Reader)new InputStreamReader(stream)).getAsJsonObject());
        }
        catch (IllegalStateException e) {
            this.loadFile(new JsonObject());
        }
        stream.close();
    }

    private void loadFile(JsonObject input) {
        for (Map.Entry entry : input.entrySet()) {
            JsonElement element = (JsonElement)entry.getValue();
            try {
                this.addFriend(element.getAsString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFriendsOld() throws IOException {
        String modName = "rebirth_friends.json";
        Path outputFile = Paths.get(modName, new String[0]);
        if (!Files.exists(outputFile, new LinkOption[0])) {
            Files.createFile(outputFile, new FileAttribute[0]);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson((JsonElement)this.writeFriends());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new OpenOption[0])));
        writer.write(json);
        writer.close();
    }

    public JsonObject writeFriends() {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        for (String str : this.friendList) {
            try {
                object.add(str.replace(" ", "_"), jp.parse(str.replace(" ", "_")));
            }
            catch (Exception exception) {}
        }
        return object;
    }

    public boolean isFriend(PlayerEntity entity) {
        return this.isFriend(entity.getName().getString());
    }
}

