package org.reunionemu.jreunion.game.items.equipment;

import org.apache.log4j.Logger;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.SpecialWeapon;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SlayerWeapon extends SpecialWeapon implements Usable {
	
	private float memoryDmg;
	
	private float demolitionDmg;
	
	private int minSkillLevel;

	public SlayerWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	public float getMemoryDmg() {
		return memoryDmg;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setMemoryDmg(0);
			setDemolitionDmg(0);
			setMinSkillLevel(0);
		} else {
			if (item.checkMembers(new String[] { "MemoryDmg" })) {
				// use member from file
				setMemoryDmg(Float.parseFloat(item.getMemberValue("MemoryDmg")));
			} else {
				// use default
				setMemoryDmg(0);
			}
			if (item.checkMembers(new String[] { "Demolition" })) {
				// use member from file
				setDemolitionDmg(Float.parseFloat(item.getMemberValue("Demolition")));
			} else {
				// use default
				setDemolitionDmg(0);
			}
			if (item.checkMembers(new String[] { "Skillevel" })) {
				// use member from file
				setMinSkillLevel(Integer.parseInt(item.getMemberValue("Skillevel")));
			} else {
				// use default
				setMinSkillLevel(0);
			}
		}
	}

	public void setMemoryDmg(float memoryDmg) {
		this.memoryDmg = memoryDmg;
	}

	public float getDemolitionDmg() {
		return demolitionDmg;
	}

	public void setDemolitionDmg(float demolitionDmg) {
		this.demolitionDmg = demolitionDmg;
	}

	public int getMinSkillLevel() {
		return minSkillLevel;
	}

	public void setMinSkillLevel(int minSkillLevel) {
		this.minSkillLevel = minSkillLevel;
	}

	@Override
	public void use(Item<?> slayerWeapon, LivingObject user, int quickSlotPosition, int unknown) {
				
		if(user instanceof Player) {
			Player player = (Player) user;
		
			if (slayerWeapon.getExtraStats() <= 0) {
				Logger.getLogger(WandWeapon.class).warn(
						"Possible cheat detected: player " + player
								+ " is trying to use empty " + this.getName()
								+ ".");
				return;
			}

			slayerWeapon.setExtraStats(slayerWeapon.getExtraStats() - 20);
			player.setStamina(player.getStamina() - getStmUsed());
			DatabaseUtils.getDinamicInstance().saveItem(slayerWeapon);
			
			if (player.getClient().getVersion() >= 2000)
				player.getClient().sendPacket(Type.UQ_ITEM, 1, quickSlotPosition,
						slayerWeapon.getEntityId(), unknown);
		}
		else
			Logger.getLogger(SlayerWeapon.class).warn(this.getName() + " not implemented for " + user.getName());
	}
}