package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.CharsExistPacket;
import org.reunionemu.jreunion.protocol.parsers.client.CharsExistParser;

public class CharsExistParserTest {

	@Test
	public void test() {
		CharsExistParser parser = new CharsExistParser();
		Pattern pattern = parser.getPattern();

		String msg = "chars_exist 1 2 myname 2 1 4 100 101 102 103 104 105 106 107 108 5 6 7 8 9 1 201 -1 203 -1 205 206 2";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CharsExistPacket);
		assertEquals(msg, packet.toString());

		CharsExistPacket charPacket = (CharsExistPacket) packet;
		assertEquals(1, charPacket.getSlot());
		assertEquals(2, charPacket.getId());
		assertEquals("myname", charPacket.getName());
		assertEquals(Race.byValue(2), charPacket.getRace());
		assertEquals(Sex.byValue(1), charPacket.getSex());
		assertEquals(4, charPacket.getHair());
		assertEquals(100, charPacket.getLevel());

		assertEquals(101, charPacket.getHp());
		assertEquals(102, charPacket.getMaxHp());
		assertEquals(103, charPacket.getMana());
		assertEquals(104, charPacket.getMaxMana());
		assertEquals(105, charPacket.getStamina());
		assertEquals(106, charPacket.getMaxStamina());
		assertEquals(107, charPacket.getElectricity());
		assertEquals(108, charPacket.getMaxElectricity());

		assertEquals(5, charPacket.getStrength());
		assertEquals(6, charPacket.getIntellect());
		assertEquals(7, charPacket.getDexterity());
		assertEquals(8, charPacket.getConstitution());
		assertEquals(9, charPacket.getLeadership());

		assertEquals(1, charPacket.getUnknown1());

		assertEquals(201, charPacket.getHelmetTypeId());
		assertEquals(-1, charPacket.getChestTypeId());
		assertEquals(203, charPacket.getPantsTypeId());
		assertEquals(-1, charPacket.getShoulderTypeId());
		assertEquals(205, charPacket.getBootsTypeId());
		assertEquals(206, charPacket.getOffhandTypeId());

		assertEquals(2, charPacket.getUnknown2());

	}

}