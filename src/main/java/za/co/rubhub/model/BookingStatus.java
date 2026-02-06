package za.co.rubhub.model;

public enum BookingStatus {
    PENDING("Pending"),
    PREPARATION("Preparation"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    ACCEPTED("Accepted"),
    NO_SHOW("No Show"),
    IN_PROGRESS("In Progress");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
