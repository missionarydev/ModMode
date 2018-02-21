package me.missionary.modmode.managers.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
public class PlayerVanishEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final boolean currentValue;
    private final boolean newValue;
    private boolean cancelled;

    public PlayerVanishEvent(Player player, boolean currentValue, boolean newValue) {
        super(player);

        this.currentValue = currentValue;
        this.newValue = newValue;
        this.cancelled = false;
    }

    public boolean getCurrentValue() {
        return currentValue;
    }

    public boolean getNewValue() {
        return newValue;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerVanishEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerVanishEvent.handlers;
    }
}
