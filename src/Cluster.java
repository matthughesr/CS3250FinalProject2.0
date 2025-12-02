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

    public void clearNodes() {
        nodes.clear();
    }

    public void clearDeployments() {
        deployments.clear();
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
        return "Cluster Name: " + name + ", status: " + status + ", nodes: " + nodes.size() + ", deployments: " + deployments.size();
    }
    
    // Will get cluster info and all deployments and format it for displaying to UI
    public String toDisplayString() {
    	//This Method was adapted with the help of Claude AI
    	// Prompt: "How can I have the cluster toString() method printout all deployments in a clean simple way?"
    	// Student review: Took advantage of example AI gave of using string builder to format everything,
    	// 					I mainly needed AI to show me the syntax and how to use string builder
        StringBuilder sb = new StringBuilder();
        sb.append("Cluster: ").append(name).append("\n")
          .append("Status: ").append(status).append("\n")
          .append("Nodes: ").append(nodes.size()).append("\n")
          .append("Deployments: ").append(deployments.size());
        
        if (!deployments.isEmpty()) {
            sb.append("\n\nDeployments:");
            for (Deployment deployment : deployments) {
                sb.append(String.format("\n• %s (%s) - %d replicas", 
                         deployment.getName(), 
                         deployment.getStatus(), 
                         deployment.getReplicas()));
            }
        }
        
        return sb.toString();
    }
    
    
    
    public Deployment getDeploymentByName(String name) {
        for (Deployment d : deployments) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }
    
    
    
}
