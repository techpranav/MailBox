package com.nilhcem.fakesmtp.gui.inbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;

public class MailDatesTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 11, 18);

	@Test
	public void formatListHandlesNull() {
		assertEquals("", MailDates.formatList(null));
		assertEquals("", MailDates.formatList(null, TODAY));
	}

	@Test
	public void formatListTodayUsesTimeOnly() {
		String value = MailDates.formatList(at(TODAY, LocalTime.of(18, 3)), TODAY);
		assertTrue(value.contains("6:03") || value.contains("18:03"));
		assertFalse(value.contains("2026"));
		assertFalse(value.toLowerCase().contains("nov"));
	}

	@Test
	public void formatListYesterday() {
		assertEquals(InboxText.get("list.date.yesterday"),
				MailDates.formatList(at(TODAY.minusDays(1), LocalTime.NOON), TODAY));
	}

	@Test
	public void formatListRecentWeekdayIsCompact() {
		String value = MailDates.formatList(at(TODAY.minusDays(3), LocalTime.NOON), TODAY);
		assertTrue(value.length() >= 2 && value.length() <= 12);
		assertFalse(value.contains("2026"));
		assertFalse(value.contains(":"));
	}

	@Test
	public void formatListSameYearOmitsYear() {
		String value = MailDates.formatList(at(LocalDate.of(2026, 3, 4), LocalTime.NOON), TODAY);
		assertTrue(value.contains("4"));
		assertFalse(value.contains("2026"));
		assertFalse(value.contains(":"));
	}

	@Test
	public void formatListPreviousYearIncludesYear() {
		String value = MailDates.formatList(at(LocalDate.of(2025, 11, 12), LocalTime.NOON), TODAY);
		assertTrue(value.contains("2025"));
		assertTrue(value.contains("12"));
	}

	@Test
	public void formatListFutureStaysCompactWithoutClock() {
		String value = MailDates.formatList(at(LocalDate.of(2026, 12, 1), LocalTime.NOON), TODAY);
		assertFalse(value.contains(":"));
		assertTrue(value.contains("1"));
	}

	@Test
	public void fullFormatStillIncludesYearAndTime() {
		String value = MailDates.format(at(TODAY, LocalTime.of(9, 15)));
		assertTrue(value.contains("2026"));
		assertTrue(value.contains("9:15") || value.contains("09:15"));
	}

	private static Date at(LocalDate day, LocalTime time) {
		LocalDateTime when = LocalDateTime.of(day, time);
		return Date.from(when.atZone(ZoneId.systemDefault()).toInstant());
	}
}
