public class Container {
    private String name;
    private String status;
    private String image;
    private String cpu;
    private String memory;
    private String diskSpace;
    
    // Constructor
    public Container(String name, String image) {
        this.name = name;
        this.image = image;
        this.status = "Pending";
        this.cpu = "0";
        this.memory = "0";
        this.diskSpace = "0";
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    
    public String getDiskSpace() { return diskSpace; }
    public void setDiskSpace(String diskSpace) { this.diskSpace = diskSpace; }
    
    // Utility method
    public String toString() {
        return "Container{name='" + name + "', status='" + status + "', image='" + image + "'}";
    }
}
