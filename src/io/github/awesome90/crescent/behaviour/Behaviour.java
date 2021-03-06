package io.github.awesome90.crescent.behaviour;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.awesome90.crescent.info.Profile;

public class Behaviour {

	private final Profile profile;

	/**
	 * The y coordinate the player was last at before they started falling.
	 */
	private double lastY;

	/**
	 * @param profile
	 *            The profile of the player whose behaviour is being analysed.
	 */
	public Behaviour(Profile profile) {
		this.profile = profile;
		this.lastY = -1.0;
	}

	/**
	 * @return If the player is in water or not.
	 */
	public final boolean isInWater() {
		Material in = getBlockPlayerIsIn().getType();
		return in == Material.WATER || in == Material.STATIONARY_WATER;
	}

	/**
	 * @return If the player is in a cobweb or not.
	 */
	public final boolean isInWeb() {
		return getBlockPlayerIsIn().getType() == Material.WEB;
	}

	/**
	 * @return If the player is on a ladder or not.
	 */
	public final boolean isOnLadder() {
		return getBlockPlayerIsIn().getType() == Material.LADDER;
	}

	/**
	 * @return If the player is on a vine or not.
	 */
	public final boolean isOnVine() {
		return getBlockPlayerIsIn().getType() == Material.VINE;
	}

	/**
	 * @return If the player is standing on a liquid block or not.
	 */
	public final boolean isOnLiquidBlock() {
		return getBlockUnderPlayer().isLiquid();
	}

	/**
	 * @return If the player is on the ground or not.
	 */
	public final boolean isOnGround() {
		final Player player = profile.getPlayer();

		final Block under = getBlockUnderPlayer();

		if (under.getType().isSolid()) {
			return true;
		}

		// The player could be shifting on the edge of a block.

		final ArrayList<Block> nearbyBlocks = getSurroundingBlocks(1);

		Block nearest = getBlockUnderPlayer();

		for (Block block : nearbyBlocks) {
			if (block.getY() == under.getY()) {
				Bukkit.broadcastMessage("test block");
				if (Math.abs(player.getLocation().getY() - block.getY()) < Math
						.abs(player.getLocation().getY() - nearest.getY())) {
					nearest = block;
				}
			}
		}

		if (nearest.getType().isSolid()) {
			return true;
		}

		return false;
	}

	public final ArrayList<Block> getSurroundingBlocks(int radius) {
		final Location currentLocation = getPlayer().getLocation();
		final World world = getPlayer().getWorld();

		ArrayList<Block> blocks = new ArrayList<Block>();

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					blocks.add(world.getBlockAt(currentLocation.getBlockX() + x, currentLocation.getBlockY() + y,
							currentLocation.getBlockZ() + z));
				}
			}
		}

		return blocks;
	}

	/**
	 * @return The block that a player is in.
	 */
	public final Block getBlockPlayerIsIn() {
		return getPlayer().getLocation().getBlock();
	}

	/**
	 * @return The block above the player.
	 */
	public final Block getBlockAbovePlayer() {
		return getBlockOnFace(BlockFace.UP);
	}

	/**
	 * @return The block under the player.
	 */
	public final Block getBlockUnderPlayer() {
		return getBlockOnFace(BlockFace.DOWN);
	}

	/**
	 * @param face
	 *            Which BlockFace to check.
	 * @return The block on this BlockFace.
	 */
	public final Block getBlockOnFace(BlockFace face) {
		return getPlayer().getLocation().getBlock().getRelative(face);
	}

	/**
	 * @param type
	 *            Get the level of a specific PotionEffect on a player.
	 * @return
	 */
	public final int getPotionEffectLevel(PotionEffectType type) {
		for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
			if (effect.getType().equals(type)) {
				return effect.getAmplifier();
			}
		}

		return 0;
	}

	/**
	 * @return If the player is ascending or not.
	 */
	public final boolean isDescending() {
		return getPlayer().getVelocity().getY() < 0.0;
	}

	/**
	 * @return If the player is ascending or not.
	 */
	public final boolean isAscending() {
		return getPlayer().getVelocity().getY() > 0.0;
	}

	/**
	 * @param level
	 *            The level of the enchantment.
	 * @param typeModifier
	 *            The type modifier of the enchantment.
	 * @return The calculated EPF.
	 */
	public final double getEPF(int level, double typeModifier) {
		return Math.floor((6 + level * level) * typeModifier / 3);
	}

	/**
	 * @return The height of the space that the player is in.
	 */
	public final int getHeightOfSpace() {
		for (int y = 0; y < getPlayer().getWorld().getMaxHeight(); y++) {
			final Location added = getPlayer().getLocation().clone().add(0, y + 1, 0);
			if (added.getBlock().getType().isSolid()) {
				return y;
			}
		}
		return 0;
	}

	/**
	 * @return The last y coordinate that the player was at the ground at.
	 */
	public final double getLastY() {
		return lastY;
	}

	/**
	 * @param lastY
	 *            The value you want to update the lastY variable to.
	 */
	public final void setLastY(double lastY) {
		this.lastY = lastY;
	}

	/**
	 * @return The distance that the player has fallen.
	 */
	public final double getFallDistance() {
		// The player cannot have a negative fall distance.
		return Math.max(lastY - getPlayer().getLocation().getY(), 0.0);
	}

	/**
	 * @return Get the Player.
	 */
	private final Player getPlayer() {
		return profile.getPlayer();
	}

}
