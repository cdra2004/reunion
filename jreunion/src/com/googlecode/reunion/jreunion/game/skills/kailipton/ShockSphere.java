package com.googlecode.reunion.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.StaffWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.game.skills.Modifier.ModifierType;
import com.googlecode.reunion.jreunion.game.skills.Modifier.ValueType;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class ShockSphere extends Tier3 implements Castable {

	public ShockSphere(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.LIGHT;
	}
	
	public float getDamageModifier(){
		/* level 1 = 6 (magic damage)
		 * level 2 = 18
		 * level 3 = 21
		 * ...
		 * level 25 = 160
		 */
		
		return (float)154/(getMaxLevel()-1);
		
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (6+((level-1)*getDamageModifier()));			
			}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 21
		 * level 2 = 22
		 * level 3 = 23
		 * ...
		 * level 25 = 45
		 */
		return 24f/(getMaxLevel()-1);
	}
	
	float getManaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (21 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, LivingObject... targets) {
		if(caster instanceof KailiptonPlayer){
			Player player = (Player)caster;
			int currentMana = player.getMana();
			int manaSpent = (int) getManaModifier(player);
			
			player.setMana(currentMana - manaSpent);
			
			Weapon weapon = player.getEquipment().getMainHand();
			float baseDamage = player.getBaseDamage();
			double weaponMagicBoost = 1;
			
			if(weapon instanceof StaffWeapon){
				weaponMagicBoost += ((double)weapon.getMagicDmg())/100; // % of magic dmg boost
			}
			
			float lightDamage = getDamageModifier(player);
			float lightningMasteryDamage = 1;
			
			// calculate damage for skills LightningBall, Lightning and LightningMastery
			for(Skill skill: ((Player)caster).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // LightningMastery
									lightningMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE: // LightningBall / Lightning
									if(skill instanceof LightningBall){
										lightDamage += (modifier.getModifier(caster)*0.5);
									}
									else if(skill instanceof Lightning){
										lightDamage += (modifier.getModifier(caster)*0.7);
									}
									break;
							}
						}
					}						
				}
			}
			
			float magicDamage = (float)((baseDamage + lightDamage) * lightningMasteryDamage * weaponMagicBoost);
			
			synchronized(targets){
				int newHp = targets[0].getHp() - (int) (magicDamage);				
				if (newHp <= 0) {
					if(targets[0] instanceof Mob)
						((Mob)targets[0]).kill((KailiptonPlayer)caster);
				} else {
					targets[0].setHp(newHp);
				}
				return true;
			}
			
		}		
		return false;
	}
}