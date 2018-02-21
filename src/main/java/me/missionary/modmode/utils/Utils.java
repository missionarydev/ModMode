package me.missionary.modmode.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Missionary (missionarymc@gmail.com) on 4/23/2017.
 */
@UtilityClass
public class Utils {

    /**
     * Can the {@link CommandSender} see the target.
     * @param sender The sender
     * @param target The target
     * @return true if the sender can see the target, false if the sender cannot see the target.
     */
    public boolean canSee(final CommandSender sender, final Player target) {
        return target != null && (!(sender instanceof Player) || ((Player) sender).canSee(target));
    }

    public Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
        Player attacker = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ede;
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player) {
                attacker = (Player)damager;
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile)damager;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player)shooter;
                }
            }

            if (attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }

        return attacker;
    }
}
