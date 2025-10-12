import java.util.ArrayList;

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
		node1.setCpu("2");
		node1.setMemory("4");
		node1.setDiskSpace("50GB");
		
		Node node2 = new Node("MattsNode2", "ARM");
		node2.setCpu("2");
		node2.setMemory("4");
		node2.setDiskSpace("50GB");
		cluster.addNode(node1);
		cluster.addNode(node2);

		// Create pods and add to nodes
		
		Pod nginxPod = new Pod("EnginxPod", "apps");
		nginxPod.setCpu("50m");
		nginxPod.setMemory("50Mi");
		nginxPod.setDiskSpace("5GB");
		nginxPod.addContainer(new Container("nginx", "nginx"));
		node1.addPod(nginxPod);

		Pod nginxPod2 = new Pod("EnginxPod2", "apps");
		nginxPod2.setCpu("50m");
		nginxPod2.setMemory("50Mi");
		nginxPod2.setDiskSpace("5GB");
		nginxPod2.addContainer(new Container("nginx", "nginx"));
		node2.addPod(nginxPod2);
		
		Pod nginxPod3 = new Pod("EnginxPod3", "apps");
		nginxPod3.setCpu("50m");
		nginxPod3.setMemory("50Mi");
		nginxPod3.setDiskSpace("5GB");
		nginxPod2.addContainer(new Container("nginx", "nginx"));
		node2.addPod(nginxPod3);
		
		

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
