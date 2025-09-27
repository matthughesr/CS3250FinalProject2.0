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
	
	public MainBorderPane(Cluster cluster) {
		this.cluster = cluster;

		/// ----------- TOP PANE ------------------------------------
		// THis pane will be used for the header. It won't change much
		HBox topPane = new HBox();
		topPane.setPrefHeight(75);
		topPane.setStyle("-fx-background-color:  #111827;"); //dark blue
		topPane.setAlignment(Pos.CENTER_LEFT);
		topPane.setPadding(new Insets(20, 0, 20, 20)); // padding: top, right, bottom, left
		
        Label headerLabel = new Label("Kubernetes Dashboard");
        headerLabel.setStyle("-fx-text-fill: #f4f4f5; -fx-font-size: 24px; -fx-font-weight: bold;"); //white
        
        topPane.getChildren().add(headerLabel); 
        setTop(topPane);
		
        // ---------------- CENTER PANE ------------------------
        // This is where most things will happen. This pane will change a lot
        defaultCenter = new VBox();
        defaultCenter.setStyle("-fx-background-color: #e5e7eb"); //dirty white
        
        Button refreshButton = new Button("Refresh(click here after creating deployment)");
        Label displayClusterLabel = new Label(cluster.toDisplayString());
        
        refreshButton.setOnAction(event -> { 
        	displayClusterLabel.setText(cluster.toDisplayString());
        });
        defaultCenter.getChildren().addAll(refreshButton, displayClusterLabel);
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
		leftPane.setStyle("-fx-background-color: #2e2f30;");
		
		//Button to create new deployment
		Button createButton = new Button("Create Deployment");
		createButton.setOnAction(event -> { 
			// Create new pane to replace current center one
			DeploymentUpsert deploymentPage = new DeploymentUpsert(() -> setCenter(defaultCenter), cluster);
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
		
		
		leftPane.getChildren().addAll(createButton, optionsLabel, podsCheck, deploymentCheck, containerCheck, statsCheck); 
		
		
		
		setLeft(leftPane);
		
//		// ------------- RIGHT PANE -------------------
//		Pane rightPane = new Pane();
//		rightPane.setPrefWidth(150);
//		rightPane.setStyle("-fx-background-color: blue;");
//		setRight(rightPane);
		
		


	}
	
	
	

    // Methods

}
