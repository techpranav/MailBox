package com.nilhcem.fakesmtp.gui.inbox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Formats message timestamps for the reading pane and the compact inbox list.
 *
 * @since 2.1
 */
public final class MailDates {

	private static final DateTimeFormatter FULL =
			DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a", Locale.getDefault());
	private static final DateTimeFormatter TIME =
			DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
	private static final DateTimeFormatter WEEKDAY =
			DateTimeFormatter.ofPattern("EEE", Locale.getDefault());
	private static final DateTimeFormatter MONTH_DAY =
			DateTimeFormatter.ofPattern("MMM d", Locale.getDefault());
	private static final DateTimeFormatter MONTH_DAY_YEAR =
			DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault());

	private MailDates() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Full timestamp for the reading pane and details.
	 */
	public static String format(Date date) {
		if (date == null) {
			return "";
		}
		return FULL.format(toLocal(date));
	}

	/**
	 * Compact relative stamp for inbox list rows (Outlook/Gmail style).
	 * <ul>
	 *   <li>Today (and future same-day clock values): {@code 6:03 PM}</li>
	 *   <li>Yesterday: {@code Yesterday}</li>
	 *   <li>Within the last six days: {@code Tue}</li>
	 *   <li>Earlier this year: {@code Nov 12}</li>
	 *   <li>Previous years: {@code Nov 12, 2025}</li>
	 * </ul>
	 */
	public static String formatList(Date date) {
		return formatList(date, LocalDate.now(ZoneId.systemDefault()));
	}

	/**
	 * Testable variant that uses an explicit "today" in the system zone.
	 */
	public static String formatList(Date date, LocalDate today) {
		if (date == null || today == null) {
			return "";
		}
		LocalDateTime when = toLocal(date);
		LocalDate day = when.toLocalDate();
		if (day.isAfter(today)) {
			// Future-dated mail: keep it compact without dumping a long absolute stamp.
			if (day.getYear() == today.getYear()) {
				return MONTH_DAY.format(when);
			}
			return MONTH_DAY_YEAR.format(when);
		}
		if (day.equals(today)) {
			return TIME.format(when);
		}
		if (day.equals(today.minusDays(1))) {
			return InboxText.get("list.date.yesterday");
		}
		if (!day.isBefore(today.minusDays(6))) {
			return WEEKDAY.format(when);
		}
		if (day.getYear() == today.getYear()) {
			return MONTH_DAY.format(when);
		}
		return MONTH_DAY_YEAR.format(when);
	}

	private static LocalDateTime toLocal(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
