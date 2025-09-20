import java.util.List;
import java.util.ArrayList;

public class Cluster {
    private String name;
    private String status;
    private List<Node> nodes;
    private List<Deployment> deployments;
    
    // Constructors
    public Cluster(String name) {
        this.name = name;
        this.status = "Unknown";
        this.nodes = new ArrayList<>();
        this.deployments = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<Node> getNodes() { return nodes; }
    
    public List<Deployment> getDeployments() { return deployments; }
    
    // Relationship methods for Nodes
    public void addNode(Node node) {
        nodes.add(node);
    }
    
    public void removeNode(Node node) {
        nodes.remove(node);
    }
    
    public int getNodeCount() {
        return nodes.size();
    }
    
    // Relationship methods for Deployments
    public void addDeployment(Deployment deployment) {
        deployments.add(deployment);
    }
    
    public void removeDeployment(Deployment deployment) {
        deployments.remove(deployment);
    }
    
    public int getDeploymentCount() {
        return deployments.size();
    }
    
    // Utility methods for dashboard
    public int getTotalPodCount() {
        int totalPods = 0;
        for (Node node : nodes) {
            totalPods += node.getPodCount();
        }
        return totalPods;
    }
    
    public int getTotalContainerCount() {
        int totalContainers = 0;
        for (Node node : nodes) {
            for (Pod pod : node.getPods()) {
                totalContainers += pod.getContainerCount();
            }
        }
        return totalContainers;
    }
    
    // Utility method
    public String toString() {
        return "Cluster{name='" + name + "', status='" + status + "', nodes=" + nodes.size() + ", deployments=" + deployments.size() + "}";
    }
}
