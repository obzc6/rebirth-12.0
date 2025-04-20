/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.events;

public class Event {
    private final Stage stage;
    private boolean cancel = false;

    public Event(Stage stage) {
        this.stage = stage;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public Stage getStage() {
        return this.stage;
    }

    public static enum Stage {
        Pre,
        Post;

        // $FF: synthetic method
        private static Event.Stage[] $values() {
            return new Event.Stage[]{Pre, Post};
        }
    }
}
