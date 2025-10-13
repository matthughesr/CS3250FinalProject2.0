import java.util.List;
import java.util.ArrayList;

public class Pod {
    private String name;
//    private String namespace;
    private String nodeName;
    private String status;
    private String ip;
    private String cpu;
    private String memory;
    private String diskSpace;
    private List<Container> containers;
    
    // Constructor
    public Pod(String name) {
        this.name = name;
//        this.namespace = namespace;
        this.status = "Pending";
        this.containers = new ArrayList<>();
        this.cpu = "0";
        this.memory = "0";
        this.diskSpace = "0";
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
//    public String getNamespace() { return namespace; }
//    public void setNamespace(String namespace) { this.namespace = namespace; }
    
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    
//    public String getImage() { return image; }
//    public void setImage(String image) { this.image = image; }
//    
    public String getDiskSpace() { return diskSpace; }
    public void setDiskSpace(String diskSpace) { this.diskSpace = diskSpace; }
    
    public List<Container> getContainers() { return containers; }
    
    // Relationship methods
    public void addContainer(Container container) {
        containers.add(container);
    }
    
    public void removeContainer(Container container) {
        containers.remove(container);
    }
    
    public int getContainerCount() {
        return containers.size();
    }
    
    // Utility method
    public String toString() {
        return "Pod{name='" + name + "', status='" + status + "', containers=" + containers.size() + "}";
    }
}
