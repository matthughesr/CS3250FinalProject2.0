import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

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
    
	public void saveYAML(String name, String namespace, Window callerWindow) {
		// Get the YAML from kubernetes 
		String yaml;
		try {
			yaml = apiInterface.fetchPodYaml(name, namespace);
		}
		catch (ApiException e) {
			System.out.println("Failed to get pod yaml: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		// Filechooser for user to choose where to save yaml file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("yaml files", "*.yaml");
		fileChooser.setInitialFileName(name + "-" + namespace + ".yaml");
		// Show dialog box to user
		File file = fileChooser.showSaveDialog(callerWindow);
		
	    // User pressed cancel
	    if (file == null) {
	        System.out.println("Save canceled.");
	        return;
	    }
	
	    	// Write to file
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
				
					writer.write(yaml);
					writer.newLine();
					System.out.println("YAML written to: " + file.getAbsolutePath());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	/**
	 * Refreshes Kubernetes data for a cluster by clearing old data and fetching fresh data.
	 *
	 * @param cluster The cluster to refresh
	 * @throws ApiException if Kubernetes API call fails
	 */
	public void refreshK8s(Cluster cluster) throws ApiException {
		System.out.println("=== Refreshing Live Data from Kubernetes ===\n");

		// Clear old data first to avoid duplicates
		cluster.clearNodes();
		cluster.clearDeployments();

		// Fetch fresh data
		apiInterface.fetchNodes(cluster);
		apiInterface.fetchPods(cluster);
		apiInterface.fetchDeployments(cluster);

		System.out.println("\n=== Live Data Refreshed Successfully ===\n");
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
