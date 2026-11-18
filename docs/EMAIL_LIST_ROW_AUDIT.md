# Email list row UX audit

## Before (previous row)

Structure (`MessageListPanel.RowRenderer`):

| Region | Content |
|--------|---------|
| West | Unread dot (or 8px strut) + avatar (30px) |
| Center / Line 1 | Sender + **date** |
| Center / Line 2 | Subject |
| Center / Line 3 | Preview |
| Date content | Flag/attachment glyphs **concatenated into the date string** |

How date wasted horizontal space:

1. `MailDates.format` used a long absolute stamp: `MMM d, yyyy, h:mm a` (e.g. `Nov 18, 2026, 6:03 PM`).
2. That measured width was subtracted from **sender, subject, and preview** alike (`textWidth = rowWidth - west - dateWidth`), so subject/preview were truncated as if a fixed date column existed on every line.
3. `dateLabel.setPreferredSize(dateWidth, …)` plus flag/attachment marks on the same label made the EAST region even wider.
4. Row height was 74px with 7px vertical padding.

## After (compact three-line row)

| Line | Content |
|------|---------|
| 1 | Avatar + unread dot · **Sender** (fills) · compact **date** (natural width, muted) |
| 2 | **Subject** (full content width, stronger weight when unread) |
| 3 | **Preview** (muted) · flag / attachment indicators only when needed |

Density:

- `LIST_ROW_HEIGHT` 74 → **66**
- Avatar 30 → **28**
- Vertical pad 7 → **5** (`LIST_ROW_PAD_V`)
- List horizontal scrollbar disabled (no per-row horizontal scroll)

Date formatter (`MailDates.formatList`):

| When | Example |
|------|---------|
| Today | `6:03 PM` |
| Yesterday | `Yesterday` |
| Last 6 days | `Tue` |
| Earlier this year | `Nov 12` |
| Previous years | `Nov 12, 2025` |

`MailDates.format` remains the full stamp for the reading pane (tooltip on list date).

## Theme

Uses existing `UiTokens` only: `TEXT`, `TEXT_MUTED`, `PREVIEW_MUTED`, `SELECTED_*`, `ACCENT`, `STAR`, `LIST_BG`, `BORDER`.

## Screenshots

- `docs/images/screenshot-list-rows-light-320.png`
- `docs/images/screenshot-list-rows-dark-320.png`
- `docs/images/screenshot-list-rows-light-220.png`
- `docs/images/screenshot-list-rows-dark-220.png`

## Limitations

- Very narrow panes (~220px) still truncate sender aggressively; date keeps its preferred width.
- Attachment indicator remains the envelope glyph (`✉`) for font compatibility.
- Smart dates follow the JVM default locale for month/weekday names; “Yesterday” is i18n via `list.date.yesterday`.
