package backend.academy.bot;

import com.pengrad.telegrambot.model.BotCommand;

public enum BotCommandEnum {
    START("/start", "Register chat"),
    END("/end", "Delete chat"),
    TRACK("/track", "Track link"),
    UNTRACK("/untrack", "Untrack link"),
    LIST("/list", "Show list of tracked links"),
    HELP("/help", "List of commands");

    private final String command;
    private final String description;

    BotCommandEnum(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public BotCommand toBotCommand() {
        return new BotCommand(command, description);
    }
}
