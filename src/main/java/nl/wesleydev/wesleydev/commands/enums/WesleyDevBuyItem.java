package nl.wesleydev.wesleydev.commands.enums;

public enum WesleyDevBuyItem {
    UNKNOWN(null),
    DIAMOND("diamond");

    private String text;

    WesleyDevBuyItem(String text) {
        this.text = text;
    }

    public static WesleyDevBuyItem fromString(String text) {
        for (WesleyDevBuyItem enumValue : WesleyDevBuyItem.values()) {
            if (enumValue.text != null && enumValue.text.equalsIgnoreCase(text)) {
                return enumValue;
            }
        }
        return WesleyDevBuyItem.UNKNOWN;
    }
}
