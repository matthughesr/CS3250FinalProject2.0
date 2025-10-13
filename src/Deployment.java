import java.util.List;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Deployment {
    private String name;
    private String status;
    private String image;
    private String cpu;
    private String memory;
    private String diskSpace;
    private int replicas;
    private String namespace;
    private List<Pod> managedPods;
    
    // Constructor
    public Deployment(String name, String image, int replicas) {
        this.name = name;
        this.image = image;
        this.replicas = replicas;
        this.status = "Pending";
        this.managedPods = new ArrayList<>();
        this.cpu = "0";
        this.memory = "0";
        this.diskSpace = "0";
        this.setNamespace("default");
        
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
    
    public int getReplicas() { return replicas; }
    public void setReplicas(int replicas) { this.replicas = replicas; }
    
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
    
    public List<Pod> getManagedPods() { return managedPods; }
    
    

    

    
    // Relationship methods
    public void addManagedPod(Pod pod) {
        managedPods.add(pod);
    }
    
    public void removeManagedPod(Pod pod) {
        managedPods.remove(pod);
    }
    
    public int getManagedPodCount() {
        return managedPods.size();
    }
    
    // Utility methods
    public String toString() {
        return "Deployment{name='" + name + "', status='" + status + "', replicas=" + replicas + ", managedPods=" + managedPods.size() + "}";
    }
    



}
