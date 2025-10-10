import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainBorderPane extends BorderPane{
	private Pane defaultCenter; // this is the "main" center pane
	private Cluster cluster;
	private ClusterManager clusterManager;

	public MainBorderPane(Cluster cluster, ClusterManager clusterManager) {
		this.cluster = cluster;
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
        	setCenter(defaultCenter);
        });

        topPane.getChildren().add(headerLabel); 
        setTop(topPane);
		
        // ---------------- CENTER PANE ------------------------
        // This is where most things will happen. This pane will change a lot
        defaultCenter = new VBox();
        defaultCenter.setStyle("-fx-background-color: #e5e7eb"); //dirty white
        defaultCenter.setPadding(new Insets(20));

        // Display all clusters
        updateClusterDisplay();

		setCenter(defaultCenter);
		
		
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
		leftPane.setStyle("-fx-background-color: #2e2f30;"); // gray
		
		
		//Button to create new Cluster
		Button createClusterButton = new Button("Create Cluster");
		createClusterButton.setOnAction(event -> {
			// Create new pane to replace current center one
			ClusterUpsert clusterPage = new ClusterUpsert(() -> setCenter(defaultCenter), clusterManager, this);
			setCenter(clusterPage);
		});
		
		//Button to create new deployment
		Button createButton = new Button("Create Deployment");
		createButton.setOnAction(event -> {
			// Create new pane to replace current center one
			DeploymentUpsert deploymentPage = new DeploymentUpsert(() -> setCenter(defaultCenter), clusterManager, this);
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
		
		
		leftPane.getChildren().addAll(createClusterButton, createButton, optionsLabel, podsCheck, deploymentCheck, containerCheck, statsCheck); 
		
		
		
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
			}
		}
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
		clusterBox.setMinHeight(150);
		clusterBox.setMaxWidth(800);

		// Cluster header with name
		Label clusterNameLabel = new Label("Cluster: " + cluster.getName());
		clusterNameLabel.setStyle(
			"-fx-font-size: 20px;" +
			"-fx-font-weight: bold;" +
			"-fx-text-fill: white;"
		);

		// Cluster info
		Label clusterInfoLabel = new Label(
			"Status: " + cluster.getStatus() +
			" | Nodes: " + cluster.getNodeCount() +
			" | Deployments: " + cluster.getDeploymentCount()
		);
		clusterInfoLabel.setStyle(
			"-fx-font-size: 14px;" +
			"-fx-text-fill: white;"
		);

		clusterBox.getChildren().addAll(clusterNameLabel, clusterInfoLabel);

		return clusterBox;
	}

}
