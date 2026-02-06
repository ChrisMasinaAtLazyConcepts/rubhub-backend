package za.co.rubhub.model;

public enum PanicAction {
    CALL_SAPS,
    NOTIFY_SECURITY_COMPANY,
    SEND_SAPS_EMAIL,
    START_LIVE_STREAM,
    STOP_LIVE_STREAM,
    RESOLVE_ALERT,
    ESCALATE,
    ADD_NOTES,
    UPDATE_STATUS
}