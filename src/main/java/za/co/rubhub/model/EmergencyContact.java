package za.co.rubhub.model;

import javax.persistence.*;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "contact_number", length = 20)
    private String contactNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public EmergencyContact() {}

    public EmergencyContact(String name, String contactNumber, String email) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getContactNumber() { 
        return contactNumber; 
    }
    
    public void setContactNumber(String contactNumber) { 
        this.contactNumber = contactNumber; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public Boolean getIsActive() { 
        return isActive; 
    }
    
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive; 
    }
}