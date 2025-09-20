public class Dashboard {
    private String name;
    private Cluster cluster;
    private ClusterMetrics metrics;

    // Constructor
    public Dashboard(String name, Cluster cluster) {
        this.name = name;
        this.cluster = cluster;
        this.metrics = null; // Will be calculated later
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Cluster getCluster() { return cluster; }
    public void setCluster(Cluster cluster) { this.cluster = cluster; }
    
    public ClusterMetrics getMetrics() { return metrics; }
    public void setMetrics(ClusterMetrics metrics) { this.metrics = metrics; }

    // Methods
    public void displayDashboard() {
        System.out.println("Dashboard: " + name);
        System.out.println("Cluster: " + cluster.getName());
    }
    
    public void refreshMetrics() {
        // Get most recent metrics
    }
    
    public void displayClusterOverview() {
        // Display basic info
    }
    
    public void displayNodeDetails() {
        // Display details about node
    }
    
    public void displayPodDetails() {
        // Display details about pod
    }
    
    public void displayDeploymentDetails() {
        // Display details about deployment
    }
    
    public void filterByNamespace(String namespace) {
        // Show pods by namespace
    }
    

}
