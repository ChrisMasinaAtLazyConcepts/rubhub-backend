package za.co.rubhub.model;

import javax.persistence.*;

@Entity
@Table(name = "security_companies")
public class SecurityCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Location location;
    
    @Column(name = "company_name", length = 100)
    private String companyName;
    
    @Column(name = "contact_number", length = 20)
    private String contactNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public SecurityCompany() {}

    public SecurityCompany(Location location, String companyName, String contactNumber, String email) {
        this.location = location;
        this.companyName = companyName;
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

    public Location getLocation() { 
        return location; 
    }
    
    public void setLocation(Location location) { 
        this.location = location; 
    }

    public String getCompanyName() { 
        return companyName; 
    }
    
    public void setCompanyName(String companyName) { 
        this.companyName = companyName; 
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