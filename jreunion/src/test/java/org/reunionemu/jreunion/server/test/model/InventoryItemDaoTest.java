package org.reunionemu.jreunion.server.test.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.*;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.model.MemoryWarpSlot;
import org.reunionemu.jreunion.model.jpa.*;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
public class InventoryItemDaoTest {

	@Autowired 
	ItemDao<Item<?>> itemDao;
	
	Map map = new MockMap(1);
	
	Player player = new MockPlayer();
	
	@Autowired
	InventoryItemDao<InventoryItem> inventoryItemDao;
	
	@Autowired
	PlayerManager playerManager;
	
	@Autowired
	ItemManager itemManager;
	
	@Test
	public void test() {
		
		assumeNotNull(playerManager);
		
		long playerId = 1;
		
		int slot = 4;
		
		assumeThat((long)player.getPlayerId(), is(playerId));
		playerManager.addPlayer(player);
						
		assumeNotNull(playerManager.getPlayerByDbId(playerId));
		
		assumeNotNull(map);
		
		assumeNotNull(itemManager);
		
		int typeId = 724;
		ItemType type = itemManager.getItemType(typeId);
		
		assumeNotNull(type);
		
		Item<?> item1 = itemManager.create(724);
		Item<?> item2 = itemManager.create(724);

		assumeNotNull(item1);
		assumeNotNull(item2);
		
		assumeNotNull(item1.getItemId());
		assumeNotNull(item2.getItemId());
		assumeThat(item1.getItemId(), is(not(item2.getItemId())));
		
		
		InventoryItem inventoryItem = new InventoryItemImpl(item1, new InventoryPosition(0,0,0), player);
		
		inventoryItemDao.save(inventoryItem);
		inventoryItem = inventoryItemDao.findByItemId(item1.getItemId());
		assertNotNull(inventoryItem);
		List<InventoryItem> inventoryItems = inventoryItemDao.findByPlayerId(playerId);
		assertThat(inventoryItems.size(), greaterThan(0));
		inventoryItems.get(0).getPosition().setPosX(10);
		inventoryItems.add(new InventoryItemImpl(item2,new ExchangePosition(0, 0), player));
		inventoryItemDao.save(inventoryItems);
		
	}
	
	private static class MockMap extends Map {

		public MockMap(int id) {
			
			super(id);
		}

		@Override
		public void handleEvent(Event event) {

		}

	}
	private static class MockPlayer extends Player{

		
		@Override
		public int getPlayerId() {
			return 1;
		}
		
		public MockPlayer() {
		}

		@Override
		public long getMaxElectricity() {
			return 0;
		}

		@Override
		public long getMaxMana() {
			return 0;
		}

		@Override
		public long getMaxStamina() {
			return 0;
		}

		@Override
		public long getBaseDamage() {
			return 0;
		}

		@Override
		public List<Skill> getDefensiveSkills() {
			return null;
		}

	
		
	}
	
}