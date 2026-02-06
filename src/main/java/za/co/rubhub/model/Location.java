package za.co.rubhub.model;

import javax.persistence.*;

@Embeddable
public class Location {
    
    @Column(name = "address", length = 200)
    private String address;
    
    @Column(name = "city", length = 50)
    private String city;
    
    @Column(name = "province", length = 50)
    private String province;
    
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;

    // Constructors
    public Location() {}

    public Location(String address, String city, String province, String postalCode) {
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    // Getters and Setters
    public String getAddress() { 
        return address; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }

    public String getCity() { 
        return city; 
    }
    
    public void setCity(String city) { 
        this.city = city; 
    }

    public String getProvince() { 
        return province; 
    }
    
    public void setProvince(String province) { 
        this.province = province; 
    }

    public String getPostalCode() { 
        return postalCode; 
    }
    
    public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }

    public Double getLatitude() { 
        return latitude; 
    }
    
    public void setLatitude(Double latitude) { 
        this.latitude = latitude; 
    }

    public Double getLongitude() { 
        return longitude; 
    }
    
    public void setLongitude(Double longitude) { 
        this.longitude = longitude; 
    }
}