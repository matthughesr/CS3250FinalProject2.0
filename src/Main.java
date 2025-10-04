import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	private static Cluster cluster;
	private static ClusterManager clusterManager;

	public static void main(String[] args) {
		// Initialize cluster manager (business logic layer)
		clusterManager = new ClusterManager();

		// Create cluster
		cluster = new Cluster("Non-Prod-Cluster");
		clusterManager.addCluster(cluster);
		
		cluster = new Cluster("Prod-Cluster");
		clusterManager.addCluster(cluster);
		
		// Create nodes and add to cluster
		Node node1 = new Node("MattsNode", "AMD");
		Node node2 = new Node("MattsNode2", "ARM");
		cluster.addNode(node1);
		cluster.addNode(node2);

		// Create pods and add to nodes
		Pod nginxPod = new Pod("EnginxPod", "apps");
		nginxPod.addContainer(new Container("nginx", "nginx"));
		node1.addPod(nginxPod);

		Pod nginxPod2 = new Pod("EnginxPod2", "apps");
		nginxPod2.addContainer(new Container("nginx", "nginx"));
		node2.addPod(nginxPod2);

		// Create deployment and add to cluster
		Deployment nginxDeployment = new Deployment("EnginxDeployment", "nginx", 2);
		nginxDeployment.addManagedPod(nginxPod);
		nginxDeployment.addManagedPod(nginxPod2);
		cluster.addDeployment(nginxDeployment);

		// Create dashboard with cluster relationship
		Dashboard dashboard = new Dashboard("MattsDashboard", cluster);
		
		// Display cluster information
		System.out.println(cluster);
		dashboard.displayDashboard();
		
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainBorderPane pane = new MainBorderPane(cluster, clusterManager); // Pass cluster and cluster manager to MainBorderPane
		Scene scene = new Scene(pane, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true); // fill the screen so I don't have to do it myself
		primaryStage.show();

	}

}
