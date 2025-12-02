// Kubernetes Java Client API imports
import io.kubernetes.client.openapi.ApiClient;        // Main client for K8s API communication
import io.kubernetes.client.openapi.ApiException;     // Exception type for K8s API errors
import io.kubernetes.client.openapi.Configuration;    // Global client configuration
import io.kubernetes.client.openapi.apis.CoreV1Api;   // Core Kubernetes API (pods, namespaces, etc.)
import io.kubernetes.client.openapi.apis.AppsV1Api;   // Apps API for deployments, replicasets, etc.
import io.kubernetes.client.util.ClientBuilder;       // Helper to build API clients
import io.kubernetes.client.util.KubeConfig;          // Kubeconfig file parser
import io.kubernetes.client.Metrics;                  // Metrics API for real-time CPU/memory data

// Regular imports
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	private static ClusterManager clusterManager;

	public static void main(String[] args) {
		

		// AI citation: The following code was created with the help of Claude code. I will likely be changing this code later so it is just proof of concept right now
		// Date: Oct 28
		// Prompt:  Right now this application display to the user basic information about a kubernetes cluster. I am just using test data right
		//now. My goal is to display actual live data from this minikube cluster usig the kubernetes java client.  How can I use the kubernetes java client to do this?
		/// Integration: Used as a starting point to interact with a live kubernetes cluster. I leaned on AI here so I can learn how to use this API since documation is not well written and avaliable
		
		// Revised AI citation (Dec1): Some of the code here is left over from AI but has been modified significicently my Matt since Oc2 28 ^^^
		try {
		// Step 1: Locate and load the kubeconfig file
			Path kubeConfigPath = getDefaultKubeconfigPath();
		
		// Step 2: Parse kubeconfig and create authenticated API client (needed so API calls to cluster will work)
			ApiClient client;
			// Read kubeconfig file
			try (FileReader fileReader = new FileReader(kubeConfigPath.toFile())) {
					KubeConfig kubeConfig = KubeConfig.loadKubeConfig(fileReader);
					client = ClientBuilder.kubeconfig(kubeConfig).build();
					Configuration.setDefaultApiClient(client);
				}
			
		// Step 3: Create API instances
			CoreV1Api coreApi = new CoreV1Api(); // for pods, nodes, namespaces, services, configmaps, secrets
			AppsV1Api appsApi = new AppsV1Api(); //  workload resources (Deployments, ReplicaSets, StatefulSets, DaemonSets
			Metrics metricsApi = new Metrics(client); // For metrics
			
		// Step 4: Create ApiInterface to interact with Kubernetes
			ApiInterface apiInterface = new ApiInterface(coreApi, appsApi, metricsApi);

			// Initialize cluster manager (business logic layer)
			clusterManager = new ClusterManager();
			clusterManager.setApiInterface(apiInterface); // inject dependecies

		// Step 5: Create cluster from real Kubernetes cluster
			String clusterName = "Minikube"; // You can get this from kubeconfig context but we will default to this name
			Cluster cluster = new Cluster(clusterName); // Initialize cluster
			clusterManager.addCluster(cluster); // Add cluster 

			System.out.println("=== Fetching Live Data from Kubernetes ===\n");

		// Step 6: Fetch cluster data using ApiInterface
			clusterManager.refreshK8s(cluster);


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
		System.out.println("Using kubeconfig: " + path);

		return path;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Load custom font 
		FontLibrary.addFont("CutiveMono", "/Fonts/CutiveMono-Regular.ttf");

		MainBorderPane pane = new MainBorderPane(clusterManager); // Pass cluster and cluster manager to MainBorderPane
		Scene scene = new Scene(pane, 500, 500);

		// Add style sheet for global styles
		// reference for css. https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm
		String cssPath = new java.io.File("src/style.css").toURI().toString();
		scene.getStylesheets().add(cssPath);

		primaryStage.setScene(scene);
		primaryStage.setMaximized(true); // fill the screen so I don't have to do it myself
		primaryStage.show();

	}

}
