import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


// Main colors:
// #111827 dark blue
// #e5e7eb dirty white
//  #2e2f30  grey


public class MainBorderPane extends BorderPane{
	private VBox defaultCenter; // this is the "main" center pane content
	private ScrollPane scrollPane; // ScrollPane wrapper for the center content
//	private Cluster cluster;
	private ClusterManager clusterManager;

	public MainBorderPane(ClusterManager clusterManager) {
//		this.cluster = cluster;
		this.clusterManager = clusterManager;
		

		/// ----------- TOP PANE ------------------------------------
		// THis pane will be used for the header. It won't change much
		HBox topPane = new HBox();
		topPane.setPrefHeight(75);
		topPane.setStyle("-fx-background-color:  #111827;"); //dark blue
		topPane.setAlignment(Pos.CENTER_LEFT);
		topPane.setPadding(new Insets(20, 0, 20, 20)); // padding: top, right, bottom, left
		
        Label headerLabel = new Label("Kubernetes Dashboard");
        headerLabel.setStyle("-fx-text-fill: #f4f4f5; -fx-font-size: 24px; -fx-font-weight: bold;"); //white
        
        // navigate to the default pane when label is clicked
        headerLabel.setOnMouseClicked(event -> {
        	refreshDefaultPane();
        	setCenter(scrollPane);
        });

        topPane.getChildren().add(headerLabel); 
        setTop(topPane);
		
        // ---------------- CENTER PANE ------------------------
        // This is where most things will happen. This pane will change a lot
        defaultCenter = new VBox();
        defaultCenter.setStyle("-fx-background-color: #e5e7eb"); //dirty white
        defaultCenter.setPadding(new Insets(20));

        // Wrap the content in a ScrollPane
        scrollPane = new ScrollPane(defaultCenter);
        scrollPane.setFitToWidth(true); // Makes content use full width
        scrollPane.setStyle("-fx-background-color: #e5e7eb;"); // Match background color

        // Scroll faster
        scrollPane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY() * 6; // Increase multiplier for faster scrolling
            scrollPane.setVvalue(scrollPane.getVvalue() - delta / scrollPane.getContent().getBoundsInLocal().getHeight());
            event.consume();
        });

        // Display all clusters
        updateClusterDisplay();

		setCenter(scrollPane);
		
		
		// ----------- BOTTOM PANE ------------------------------
		// This is going to be used for the footer. Will stay pretty static
		HBox bottomPane = new HBox();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(20, 0, 20, 20));
		bottomPane.setPrefHeight(50);
		bottomPane.setStyle("-fx-background-color:  #111827;"); //dark blue
		
		Label footerLabel = new Label("Matthew Hughes -- WSU CS3250 -- FALL 2025");
		footerLabel.setStyle("-fx-text-fill: #f4f4f5; -fx-font-size: 18px; -fx-font-weight: bold;");
		bottomPane.getChildren().add(footerLabel);
		setBottom(bottomPane);
		
		
		// -------------- LEFT PANE ------------------------
		// This pane is used for navigation. 
		VBox leftPane = new VBox(20); //20 sets the padding between nodes
		leftPane.setPadding(new Insets(20, 20, 20, 20));
		leftPane.setAlignment(Pos.TOP_LEFT); 
		leftPane.setPrefWidth(200);
		leftPane.setStyle("-fx-background-color: #2e2f30;"); // grey
		
		
		//Button to create new Cluster
		Button createClusterButton = new Button("Create Cluster");
		createClusterButton.setOnAction(event -> {
			// Create new pane to replace current center one
			ClusterUpsert clusterPage = new ClusterUpsert(() -> setCenter(scrollPane), clusterManager, this);
			setCenter(clusterPage);
		});

		//Button to create new deployment
		Button createButton = new Button("Create Deployment");
		createButton.setOnAction(event -> {
			// Create new pane to replace current center one
			DeploymentUpsert deploymentPage = new DeploymentUpsert(() -> setCenter(scrollPane), clusterManager, this);
			setCenter(deploymentPage);
	    });
		
		Label optionsLabel = new Label("Options");
		
		// Check Boxes
		// Will be used to filter what is displayed
		CheckBox podsCheck = new CheckBox("Pods");
		CheckBox deploymentCheck = new CheckBox("Deployment");
		CheckBox containerCheck = new CheckBox("Container");
		CheckBox statsCheck = new CheckBox("Stats");
		String checkBoxStyle = "-fx-text-fill: #f4f4f5; -fx-font-size: 16px; -fx-font-weight: bold;";
		podsCheck.setStyle(checkBoxStyle);
		deploymentCheck.setStyle(checkBoxStyle);
		containerCheck.setStyle(checkBoxStyle);
		statsCheck.setStyle(checkBoxStyle);
		
		optionsLabel.setStyle("-fx-text-fill: #f4f4f5; -fx-font-size: 18px;");
		
		
//		leftPane.getChildren().addAll(createClusterButton, createButton, optionsLabel, podsCheck, deploymentCheck, containerCheck, statsCheck); 
		leftPane.getChildren().addAll(createClusterButton, createButton); 
		
		
		
		setLeft(leftPane);
		
//		// ------------- RIGHT PANE -------------------
//		Pane rightPane = new Pane();
//		rightPane.setPrefWidth(150);
//		rightPane.setStyle("-fx-background-color: blue;");
//		setRight(rightPane);
		

	}
	
	
	

    // Methods
	
	// Public facing method for updating center pane display
	public void refreshDefaultPane() {
		updateClusterDisplay(); 
	}

	
	
	// This will make sure that the labels displaying the cluster info is up to date
	private void updateClusterDisplay() {
		// Clear the current display
		defaultCenter.getChildren().clear();

		// Add title
		Label titleLabel = new Label("All Clusters");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");
		defaultCenter.getChildren().add(titleLabel);

		// Get clusters from business logic layer
		List<Cluster> clusters = clusterManager.getAllClusters();

		// Loop through all clusters and display each one
		if (clusters.isEmpty()) {
			Label emptyLabel = new Label("No clusters yet. Create one to get started!");
			emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");
			defaultCenter.getChildren().add(emptyLabel);
		} else {
			for (Cluster c : clusters) {
				// Create a visual box for each cluster
				VBox clusterBox = createClusterBox(c);
				defaultCenter.getChildren().add(clusterBox);
				
				// Loop through each node in the cluster and create a node box
				for (Node node : c.getNodes()) {
					FlowPane nodeBox = createNodeBox(node);
					clusterBox.getChildren().add(nodeBox);
					
					// Loop through each pod in the node and create pod box
					for(Pod pod : node.getPods()) {
						HBox podBox = createPodBox(pod);
						nodeBox.getChildren().add(podBox);
					}
				}
			}
		}
	}
	// This method will create a HBox to visually represent a pod
	
	private HBox createPodBox(Pod pod) {
		HBox podBox = new HBox(10);
		podBox.setPadding(new Insets(10));
		podBox.setStyle(
				"-fx-background-color: #e5e7eb;" + // dirty white
				"-fx-border-radius: 10;" +
				"-fx-background-radius: 10;"
			);

		// Create a VBox to hold pod information labels
		VBox podInfoBox = new VBox(5);
		podInfoBox.setPadding(new Insets(5));

		// Header label to identify pod
		Label podNameLabel = new Label("Pod: " + pod.getName());
		podNameLabel.setStyle(
			"-fx-font-size: 16px;" +
			"-fx-font-weight: bold;" +
			"-fx-text-fill: black;"
		);

		
		// Additional pod information labels
//		Deployment deployment = cluster.getDeploymentByName("");
//		Label namespaceLabel = new Label("Namespace: " + deployment.getNamespace());
//		namespaceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

//		Label nodeNameLabel = new Label("Node Name: " + pod.getNodeName());
//		nodeNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

		Label statusLabel = new Label("Status: " + pod.getStatus());
		statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
		
		Label namespaceLabel = new Label("Namespace: " + pod.getNamespace());
		namespaceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
		
		Button detailsButton = new Button("Details");
		detailsButton.setOnAction(event -> {
			// Create new pane to replace current center one
			PodDetailsPane podPane = new PodDetailsPane(() -> setCenter(scrollPane), pod, this );
			setCenter(podPane);
		});
		detailsButton.setStyle( "-fx-background-color: #111827; -fx-text-fill: white; -fx-padding: 6 12; -fx-background-radius: 6;-fx-font-size: 14px;");


		
		podInfoBox.getChildren().addAll(podNameLabel, statusLabel, namespaceLabel, detailsButton);

		podBox.getChildren().add(podInfoBox);

		return podBox;
	}
	
	// This method will create a HBox to visually represent a node
	private FlowPane createNodeBox(Node node) {
		FlowPane nodeBox = new FlowPane();
		nodeBox.setVgap(8);
		nodeBox.setHgap(4);
		nodeBox.setPrefWrapLength(300); // preferred width = 300
		nodeBox.setPadding(new Insets(10));
		VBox.setMargin(nodeBox, new Insets(0, 0, 10, 0)); // Add bottom margin for spacing
		nodeBox.setStyle(
			"-fx-background-color: #2e2f30;" + // grey
			"-fx-border-radius: 10;" +
			"-fx-background-radius: 10;"
		);

		// Create a VBox to hold node information labels
		VBox nodeInfoBox = new VBox(5);
		nodeInfoBox.setPadding(new Insets(5));

		// Header label to identify node
		Label nodeNameLabel = new Label("Node: " + node.getName());
		nodeNameLabel.setStyle(
			"-fx-font-size: 16px;" +
			"-fx-font-weight: bold;" +
			"-fx-text-fill: white;"
		);

		// Additional node information labels
		Label statusLabel = new Label("Status: " + node.getStatus());
		statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

		Label architectureLabel = new Label("Architecture: " + node.getArchitecture());
		architectureLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
		
		Label podNumLabel = new Label("Pods: " + node.getPodCount());
		podNumLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

		Label cpuLabel = new Label("CPU: " + node.getCpu());
		cpuLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

		Label memoryLabel = new Label("Memory: " + node.getMemory());
		memoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

		Label diskSpaceLabel = new Label("Disk Space: " + node.getDiskSpace());
		diskSpaceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

		// Add all labels to the info box
		nodeInfoBox.getChildren().addAll(nodeNameLabel, podNumLabel,statusLabel, architectureLabel,
		                                   cpuLabel, memoryLabel, diskSpaceLabel);

		nodeBox.getChildren().add(nodeInfoBox);

		return nodeBox;
	}
	
	
	// AI Citation: Claude Code was used here for the help of the overall logic of the method
	// This method will create a vbox to visually represent a cluster
	private VBox createClusterBox(Cluster cluster) {
		VBox clusterBox = new VBox(10);
		clusterBox.setPadding(new Insets(20));
		VBox.setMargin(clusterBox, new Insets(0, 0, 15, 0)); // Add bottom margin for spacing
		clusterBox.setStyle(
			"-fx-background-color: #111827;" + // Blue background
			"-fx-border-radius: 10;" +
			"-fx-background-radius: 10;"
		);

		// Cluster header with name
		Label clusterNameLabel = new Label("Cluster: " + cluster.getName());
		clusterNameLabel.setStyle(
			"-fx-font-size: 20px;" +
			"-fx-font-weight: bold;" +
			"-fx-text-fill: white;"
		);

		// Cluster info
		Label clusterInfoLabel = new Label(
			"Nodes: " + cluster.getNodeCount() +
			" | Deployments: " + cluster.getDeploymentCount()
		);
		clusterInfoLabel.setStyle(
			"-fx-font-size: 14px;" +
			"-fx-text-fill: white;"
		);

		// Add cluster header and info to the box
		clusterBox.getChildren().addAll(clusterNameLabel, clusterInfoLabel);

		return clusterBox;
	}

	// Getter for ClusterManager
	public ClusterManager getClusterManager() {
		return clusterManager;
	}

}
