package za.co.rubhub.model;

public class FilterOptions {
    private String type;
    private String serviceType;
    private Double maxPrice;
    private Double rating;
    private GenderPreference genderPreference;
    private Boolean availableNow;
    private String category;

    public enum GenderPreference {
        ANY, MALE, FEMALE
    }

    // Constructors
    public FilterOptions() {}

    public FilterOptions(String type, String serviceType, Double maxPrice, Double rating, 
                        GenderPreference genderPreference, Boolean availableNow, String category) {
        this.type = type;
        this.serviceType = serviceType;
        this.maxPrice = maxPrice;
        this.rating = rating;
        this.genderPreference = genderPreference;
        this.availableNow = availableNow;
        this.category = category;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public GenderPreference getGenderPreference() { return genderPreference; }
    public void setGenderPreference(GenderPreference genderPreference) { this.genderPreference = genderPreference; }

    public Boolean getAvailableNow() { return availableNow; }
    public void setAvailableNow(Boolean availableNow) { this.availableNow = availableNow; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}