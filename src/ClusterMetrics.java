public class ClusterMetrics {
    int totalNodes;
    int totalPods;
    int totalContainers;
    double totalCpuUsage;
    double totalMemoryUsage;

    // Constructor
    public ClusterMetrics(int totalNodes, int totalPods, int totalContainers, double totalCpuUsage, double totalMemoryUsage) {
        this.totalNodes = totalNodes;
        this.totalPods = totalPods;
        this.totalContainers = totalContainers;
        this.totalCpuUsage = totalCpuUsage;
        this.totalMemoryUsage = totalMemoryUsage;
    }


    // Getters and Setters
    public int getTotalNodes() { return totalNodes; }
    public void setTotalNodes(int totalNodes) { this.totalNodes = totalNodes; }

    public int getTotalPods() { return totalPods; }
    public void setTotalPods(int totalPods) { this.totalPods = totalPods; }
    
    public int getTotalContainers() { return totalContainers; }
    public void setTotalContainers(int totalContainers) { this.totalContainers = totalContainers; }

    public double getTotalCpuUsage() { return totalCpuUsage; }
    public void setTotalCpuUsage(double totalCpuUsage) { this.totalCpuUsage = totalCpuUsage; }
    
    
    public double getTotalMemoryUsage() { return totalMemoryUsage; }
    public void setTotalMemoryUsage(double totalMemoryUsage) { this.totalMemoryUsage = totalMemoryUsage; }

    // Methods
    public void getMetrics() {
    }

    public void displayMetrics() {
        System.out.println("Total Nodes: " + totalNodes);
        System.out.println("Total Pods: " + totalPods);
        System.out.println("Total Containers: " + totalContainers);
        System.out.println("Total CPU Usage: " + totalCpuUsage);
        System.out.println("Total Memory Usage: " + totalMemoryUsage);
    }
    
}
