import java.util.List;
import java.util.ArrayList;

public class Node {
    private String name;
    private String status;
    private String architecture;
    private String cpu;
    private String memory;
    private String diskSpace;
    private List<Pod> pods;
    
    // Constructor
    public Node(String name, String architecture) {
        this.name = name;
        this.architecture = architecture;
        this.status = "Ready";
        this.pods = new ArrayList<>();
        this.cpu = "0";
        this.memory = "0";
        this.diskSpace = "0";
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getArchitecture() { return architecture; }
    public void setArchitecture(String architecture) { this.architecture = architecture; }
    
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    
    public String getDiskSpace() { return diskSpace; }
    public void setDiskSpace(String diskSpace) { this.diskSpace = diskSpace; }
    
    public List<Pod> getPods() { return pods; }
    
    // Relationship methods
    public void addPod(Pod pod) {
        pods.add(pod);
        pod.setNodeName(this.name);
    }
    
    public void removePod(Pod pod) {
        pods.remove(pod);
        pod.setNodeName(null);
    }
    
    public int getPodCount() {
        return pods.size();
    }

    public void clearPods() {
        pods.clear();
    }

    // Utility method
    public String toString() {
        return "Node{name='" + name + "', status='" + status + "', architecture='" + architecture + "', pods=" + pods.size() + "}";
    }
}
