// Kubernetes Java Client API imports
import io.kubernetes.client.openapi.ApiClient;        // Main client for K8s API communication
import io.kubernetes.client.openapi.ApiException;     // Exception type for K8s API errors
import io.kubernetes.client.openapi.Configuration;    // Global client configuration
import io.kubernetes.client.openapi.apis.CoreV1Api;   // Core Kubernetes API (pods, namespaces, etc.)
import io.kubernetes.client.openapi.apis.AppsV1Api;   // Apps API for deployments, replicasets, etc.
import io.kubernetes.client.openapi.models.*;         // All Kubernetes object models
import io.kubernetes.client.custom.Quantity;          // For resource quantities (CPU, memory)
import io.kubernetes.client.util.ClientBuilder;       // Helper to build API clients
import io.kubernetes.client.util.KubeConfig;          // Kubeconfig file parser



// Regular imports
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
//	private static Cluster cluster;
//	private static Cluster cluster2;
	private static ClusterManager clusterManager;

	public static void main(String[] args) {
		// Initialize cluster manager (business logic layer)
		clusterManager = new ClusterManager();
		
		
		
		// AI citation: The following code was created with the help of Claude code. I will likely be changing this code later so it is just proof of concept right now
		// Date: Oct 28
		// Prompt:  Right now this application display to the user basic information about a kubernetes cluster. I am just using test data right
		//now. My goal is to display actual live data from this minikube cluster usig the kubernetes java client.  How can I use the kubernetes java client to do this?
		/// Integration: Used as a starting point to interact with a live kubernetes cluster. I leaned on AI here so I can learn how to use this API since documation is not well written and avaliable 

		try {
			// Step 1: Locate and load the kubeconfig file
			Path kubeConfigPath = getDefaultKubeconfigPath();
			System.out.println("Using kubeconfig: " + kubeConfigPath);

			// Step 2: Parse kubeconfig and create authenticated API client
			try (FileReader fileReader = new FileReader(kubeConfigPath.toFile())) {
				KubeConfig kubeConfig = KubeConfig.loadKubeConfig(fileReader);
				ApiClient client = ClientBuilder.kubeconfig(kubeConfig).build();
				Configuration.setDefaultApiClient(client);
			}

			// Step 3: Create API instances
			CoreV1Api coreApi = new CoreV1Api();
			AppsV1Api appsApi = new AppsV1Api();

			// Step 4: Create cluster from real Kubernetes cluster
			String clusterName = "minikube"; // You can get this from kubeconfig context
			Cluster cluster = new Cluster(clusterName);
			clusterManager.addCluster(cluster);

			System.out.println("=== Fetching Live Data from Kubernetes ===\n");
			
			// step 5: get nodes and add to cluster
			// Map to store node objects for later pod assignment
			Map<String, Node> nodeMap = new HashMap<>();
			getNodes(cluster, nodeMap);

			// Step 6: Fetch and populate pods
			System.out.println("\nFetching pods...");
			V1PodList podList = coreApi.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);

			// Map to store pods by name for later deployment assignment
			Map<String, Pod> podMap = new HashMap<>();

			for (V1Pod k8sPod : podList.getItems()) {
				String podName = k8sPod.getMetadata().getName();
				String namespace = k8sPod.getMetadata().getNamespace();
				String nodeName = k8sPod.getSpec().getNodeName();

				// Create pod object
				Pod pod = new Pod(podName);

				// Add containers to pod
				for (V1Container container : k8sPod.getSpec().getContainers()) {
					String containerName = container.getName();
					String image = container.getImage();
					pod.addContainer(new Container(containerName, image));
				}

				// Get resource requests if available
				if (k8sPod.getSpec().getContainers().size() > 0) {
					V1Container firstContainer = k8sPod.getSpec().getContainers().get(0);
					if (firstContainer.getResources() != null && firstContainer.getResources().getRequests() != null) {
						Map<String, Quantity> requests = firstContainer.getResources().getRequests();
						if (requests.containsKey("cpu")) {
							pod.setCpu(requests.get("cpu").toSuffixedString());
						}
						if (requests.containsKey("memory")) {
							pod.setMemory(requests.get("memory").toSuffixedString());
						}
					}
				}

				// Add pod to appropriate node
				if (nodeName != null && nodeMap.containsKey(nodeName)) {
					nodeMap.get(nodeName).addPod(pod);
				}

				podMap.put(namespace + "/" + podName, pod);

				System.out.println("  - Pod: " + podName + " (Namespace: " + namespace + ", Node: " + nodeName + ")");
			}

			// Step 7: Fetch and populate deployments
			System.out.println("\nFetching deployments...");
			V1DeploymentList deploymentList = appsApi.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null, null);

			for (V1Deployment k8sDeployment : deploymentList.getItems()) {
				String deploymentName = k8sDeployment.getMetadata().getName();
				String namespace = k8sDeployment.getMetadata().getNamespace();
				Integer replicas = k8sDeployment.getSpec().getReplicas();

				// Get the container image from the pod template
				String image = "unknown";
				if (k8sDeployment.getSpec().getTemplate().getSpec().getContainers().size() > 0) {
					image = k8sDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
				}

				// Create deployment object
				Deployment deployment = new Deployment(deploymentName, image, replicas != null ? replicas : 0);

				// Try to find and associate pods managed by this deployment
				// Note: This is a simplified approach - in reality you'd match by labels
				String deploymentPrefix = deploymentName + "-";
				for (Map.Entry<String, Pod> entry : podMap.entrySet()) {
					if (entry.getKey().startsWith(namespace + "/" + deploymentPrefix)) {
						deployment.addManagedPod(entry.getValue());
					}
				}

				cluster.addDeployment(deployment);

				System.out.println("  - Deployment: " + deploymentName + " (Namespace: " + namespace + ", Replicas: " + replicas + ", Image: " + image + ")");
			}

			System.out.println("\n=== Live Data Loaded Successfully ===\n");

			// Create dashboard with cluster relationship
			Dashboard dashboard = new Dashboard("Kubernetes Dashboard", cluster);

			// Display cluster information
			System.out.println(cluster);
			dashboard.displayDashboard();

		} catch (ApiException e) {
			// Handle Kubernetes API-specific errors
			System.err.println("Kubernetes API error: " + e.getCode() + " - " + e.getResponseBody());
			e.printStackTrace();
		} catch (Exception e) {
			// Handle any other errors
			System.err.println("Error loading Kubernetes data: " + e.getMessage());
			e.printStackTrace();
		}

		// Launch JavaFX application
		launch(args);
	}
	
	
	
	
	private static void getNodes(Cluster cluster, Map<String, Node> nodeMap) {

		// Step 5: Fetch and populate nodes
		System.out.println("Fetching nodes...");
		V1NodeList nodeList = coreApi.listNode(null, null, null, null, null, null, null, null, null, null, null);



		for (V1Node k8sNode : nodeList.getItems()) {
			String nodeName = k8sNode.getMetadata().getName();
			String architecture = k8sNode.getStatus().getNodeInfo().getArchitecture();

			// Get node capacity
			Map<String, Quantity> capacity = k8sNode.getStatus().getCapacity();
			String cpu = capacity.get("cpu").toSuffixedString();
			String memory = capacity.get("memory").toSuffixedString();
			String storage = capacity.containsKey("ephemeral-storage") ?
				capacity.get("ephemeral-storage").toSuffixedString() : "Unknown";

			// Create node object
			Node node = new Node(nodeName, architecture);
			node.setCpu(cpu);
			node.setMemory(memory);
			node.setDiskSpace(storage);

			cluster.addNode(node);
			nodeMap.put(nodeName, node);

			System.out.println("  - Node: " + nodeName + " (CPU: " + cpu + ", Memory: " + memory + ")");
		}

	}

	/**
	 * Locates the default kubeconfig file on the system.
	 * 
	 * On Windows, this is typically: C:\Users\{username}\.kube\config
	 * On Unix/Linux/Mac, this is typically: ~/.kube/config
	 * 
	 * @return Path to the kubeconfig file
	 * @throws IllegalStateException if kubeconfig file doesn't exist
	 */
	private static Path getDefaultKubeconfigPath() {
		// Try Windows-style environment variable first (USERPROFILE)
		String home = System.getenv("USERPROFILE");
		if (home == null || home.isEmpty()) {
			// Fall back to cross-platform user.home system property
			home = System.getProperty("user.home");
		}
		
		// Build path to standard kubeconfig location
		Path path = Paths.get(home, ".kube", "config");
		
		// Verify the file exists before returning
		if (!Files.exists(path)) {
			throw new IllegalStateException("kubeconfig not found at " + path + ". Ensure kubectl is configured (minikube)." );
		}
		return path;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainBorderPane pane = new MainBorderPane(clusterManager); // Pass cluster and cluster manager to MainBorderPane
		Scene scene = new Scene(pane, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true); // fill the screen so I don't have to do it myself
		primaryStage.show();

	}

}
