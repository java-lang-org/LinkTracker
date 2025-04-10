package backend.academy.bot;

public enum BotStateType {
    DEFAULT,
    WAITING_TRACKED_URL,
    WAITING_TAG,
    WAITING_TAGS,
    WAITING_FILTER,
    WAITING_UNTRACKED_URL
}
