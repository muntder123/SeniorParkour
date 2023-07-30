package dev.core.api.map.enums;

public enum CompletionReason {
    /**
     * End reason
     * NormalCompletionReason : use when you want to add support for another plugins or other stuff
     */
    NormalCompletionReason,
    /**
     * ForcedCompletionReason : Used when a null game end , for example
     * User started a parkour but didn't finish it in time, it will call the ForceCompletionReason!
     */
    ForcedCompletionReason;
}
