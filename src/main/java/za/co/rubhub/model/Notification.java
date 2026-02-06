package za.co.rubhub.model;

import javax.persistence.*;

@Embeddable
public class Notification {
    
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    private Boolean smsNotifications = true;
    
    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;
    
    @Column(name = "emergency_alerts")
    private Boolean emergencyAlerts = true;
    
    @Column(name = "security_alerts")
    private Boolean securityAlerts = true;

    // Constructors
    public Notification() {}

    public Notification(Boolean emailNotifications, Boolean smsNotifications, Boolean pushNotifications) {
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
        this.pushNotifications = pushNotifications;
        this.emergencyAlerts = true;
        this.securityAlerts = true;
    }

    // Getters and Setters
    public Boolean getEmailNotifications() { 
        return emailNotifications; 
    }
    
    public void setEmailNotifications(Boolean emailNotifications) { 
        this.emailNotifications = emailNotifications; 
    }

    public Boolean getSmsNotifications() { 
        return smsNotifications; 
    }
    
    public void setSmsNotifications(Boolean smsNotifications) { 
        this.smsNotifications = smsNotifications; 
    }

    public Boolean getPushNotifications() { 
        return pushNotifications; 
    }
    
    public void setPushNotifications(Boolean pushNotifications) { 
        this.pushNotifications = pushNotifications; 
    }

    public Boolean getEmergencyAlerts() { 
        return emergencyAlerts; 
    }
    
    public void setEmergencyAlerts(Boolean emergencyAlerts) { 
        this.emergencyAlerts = emergencyAlerts; 
    }

    public Boolean getSecurityAlerts() { 
        return securityAlerts; 
    }
    
    public void setSecurityAlerts(Boolean securityAlerts) { 
        this.securityAlerts = securityAlerts; 
    }
}