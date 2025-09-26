import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainBorderPane extends BorderPane{
	public MainBorderPane() {

		/// ----------- TOP PANE ------------------------------------
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
		Pane centerPane = new Pane();
		centerPane.setStyle("-fx-background-color: #e5e7eb"); //dirty white
		setCenter(centerPane);
		
		
		// ----------- BOTTOM PANE ------------------------------
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
		VBox leftPane = new VBox(20); //20 sets the padding between nodes
		leftPane.setPadding(new Insets(20, 20, 20, 20));
		leftPane.setAlignment(Pos.TOP_LEFT); 
		leftPane.setPrefWidth(150);
		leftPane.setStyle("-fx-background-color: #2e2f30;");
		
		Button editButton = new Button("Create/Edit Deployment");
		Label optionsLabel = new Label("Options");
		
		// Check Boxes
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
		
		
		leftPane.getChildren().addAll(editButton, optionsLabel, podsCheck, deploymentCheck, containerCheck, statsCheck); 
		
		
		
		setLeft(leftPane);
		
//		// ------------- RIGHT PANE -------------------
//		Pane rightPane = new Pane();
//		rightPane.setPrefWidth(150);
//		rightPane.setStyle("-fx-background-color: blue;");
//		setRight(rightPane);
		
		


	}

}
