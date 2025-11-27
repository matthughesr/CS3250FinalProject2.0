import java.util.ArrayList;
import java.util.List;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;

//Business logic class for managing Kubernetes clusters
// This is a new class that is key for seperating GUI and buisness logic
// They key function of this class is keeping track of clusters
 
 
public class ClusterManager {
	// List for clusters
    private List<Cluster> clusters;
    private ApiInterface apiInterface;

    public ClusterManager() {
        this.clusters = new ArrayList<>();
    }

    // Set the API interface for interacting with Kubernetes
    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    // Get the API interface
    public ApiInterface getApiInterface() {
        return apiInterface;
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

    // returns a cluster based on its name
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

    /**
     * Creates a deployment in the Kubernetes cluster and adds it to the local cluster model.
     *
     * @param clusterName Name of cluster to add deployment to
     * @param deploymentName Name of the deployment
     * @param image Container image
     * @param replicas Number of replicas
     * @param cpu CPU in millicores (will be converted to "XXXm" format)
     * @param memory Memory in Mi (will be converted to "XXXMi" format)
     * @param diskSpace Disk space in Mi
     * @throws ApiException if Kubernetes API call fails
     * @throws IllegalArgumentException if cluster not found
     */
    public void createDeploymentInCluster(
            String clusterName,
            String deploymentName,
            String image,
            int replicas,
            String cpu,
            String memory,
            String diskSpace) throws ApiException {

        // 1. Find the cluster
        Cluster cluster = getClusterByName(clusterName);
        if (cluster == null) {
            throw new IllegalArgumentException("Cluster not found: " + clusterName);
        }

        // 2. Verify apiInterface is set
        if (apiInterface == null) {
            throw new IllegalStateException("ApiInterface not set. Cannot create deployment.");
        }

        // 3. Format resource strings for Kubernetes
        String cpuFormatted = cpu + "m";        // "500" -> "500m"
        String memoryFormatted = memory + "Mi"; // "512" -> "512Mi"
        String diskFormatted = diskSpace + "Mi"; // "256" -> "256Mi"

        // 4. Call ApiInterface to create in Kubernetes
        V1Deployment k8sDeployment = apiInterface.createDeployment(
            deploymentName,
            image,
            replicas,
            cpuFormatted,
            memoryFormatted,
            diskFormatted,
            "default"  // Always use default namespace per requirements
        );

        // 5. Create local Deployment object and add to cluster
        // Only do this if Kubernetes creation succeeded (no exception thrown)
        Deployment localDeployment = new Deployment(deploymentName, image, replicas);
        localDeployment.setCpu(cpu);
        localDeployment.setMemory(memory);
        localDeployment.setDiskSpace(diskSpace);

        cluster.addDeployment(localDeployment);

        System.out.println("Deployment added to local cluster model: " + deploymentName);
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
