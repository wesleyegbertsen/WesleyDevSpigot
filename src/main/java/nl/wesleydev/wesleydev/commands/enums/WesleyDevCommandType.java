package nl.wesleydev.wesleydev.commands.enums;

public enum WesleyDevCommandType {
    UNKNOWN(null),
    BUY("buy"),
    PRICE("price");

    private String text;

    WesleyDevCommandType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static WesleyDevCommandType fromString(String text) {
        for (WesleyDevCommandType enumValue : WesleyDevCommandType.values()) {
            if (enumValue.text != null && enumValue.text.equalsIgnoreCase(text)) {
                return enumValue;
            }
        }
        return WesleyDevCommandType.UNKNOWN;
    }
}
