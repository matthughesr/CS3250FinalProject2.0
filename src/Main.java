import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

	public static void main(String[] args) {
		// Create cluster
		Cluster cluster = new Cluster("MattsCluster");
		
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
		// TODO Auto-generated method stub
		MainBorderPane pane = new MainBorderPane(); // Splits into 5 boxes
		Scene scene = new Scene(pane, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
