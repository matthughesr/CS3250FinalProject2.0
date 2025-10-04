import java.util.ArrayList;
import java.util.List;

//Business logic class for managing Kubernetes clusters
// This is a new class that is key for seperating GUI and buisness logic
// They key function of this class is keeping track of clusters
 
 
public class ClusterManager {
	// List for clusters
    private List<Cluster> clusters;

    public ClusterManager() {
        this.clusters = new ArrayList<>();
    }

    // Cluster management methods
    
    // add a new cluster
    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    // remove the cluster
    public void removeCluster(Cluster cluster) {
        clusters.remove(cluster);
    }

    // return list of all current clusters
    public List<Cluster> getAllClusters() {
        return clusters;
    }

    // return the name of a specific cluster
    public Cluster getClusterByName(String name) {
        for (Cluster c : clusters) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    // get the number of clusters
    public int getClusterCount() {
        return clusters.size();
    }

    // Deployment management methods
    
    // add a deployment to a cluster
    public void addDeploymentToCluster(String clusterName, Deployment deployment) {
        Cluster cluster = getClusterByName(clusterName);
        if (cluster != null) {
            cluster.addDeployment(deployment);
        }
    }

    // remove deployments from cluster
    public void removeDeploymentFromCluster(String clusterName, Deployment deployment) {
        Cluster cluster = getClusterByName(clusterName);
        if (cluster != null) {
            cluster.removeDeployment(deployment);
        }
    }

    // Methods to collect metrics 
    public int getTotalNodeCount() {
        int total = 0;
        for (Cluster c : clusters) {
            total += c.getNodeCount();
        }
        return total;
    }

    public int getTotalDeploymentCount() {
        int total = 0;
        for (Cluster c : clusters) {
            total += c.getDeploymentCount();
        }
        return total;
    }

    public int getTotalPodCount() {
        int total = 0;
        for (Cluster c : clusters) {
            total += c.getTotalPodCount();
        }
        return total;
    }
}
