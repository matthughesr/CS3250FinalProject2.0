import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// This is a new class for this assignment. 
// This class currently will create a form for creating a new class
// It allows you to create a new cluster. Right now only "name" is required 

public class ClusterUpsert extends VBox{
    private ClusterManager clusterManager;
    private MainBorderPane mainBorderPane;

    // AI Citation:
    // Task: Used ChatGPT to learn how to switch out panes and go back and forth between them
    // Integration: Took advantage of Runnable to restore original center pane when returning
    public ClusterUpsert(Runnable goBack, ClusterManager clusterManager, MainBorderPane mainBorderPane) {
        this.clusterManager = clusterManager;
        this.mainBorderPane = mainBorderPane;

        //styling
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #e5e7eb"); //dirty white

        // Title (only this is centered)
        Label titleLabel = new Label("Create a new Cluster");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");

        HBox titleBox = new HBox(titleLabel, errorLabel); // Add labels to HBox pane
        titleBox.setAlignment(Pos.CENTER); // Center the title horizontally
        getChildren().addAll(titleBox, errorLabel);

        // This is the main part of the form
        // This is where I will collect user input
        // Cluster Name
        Label clusterNameLabel = new Label("Cluster Name:");
        TextField clusterNameTextField = new TextField();
        clusterNameTextField.setMaxWidth(250);

        // Create button. This will validate the input and create cluster object
        Button createButton = new Button("Create");
        createButton.setOnAction(event -> {
            errorLabel.setText(""); // clear out old errors
            // Get info from text box
            String name = clusterNameTextField.getText();

            // Validate the data
            if (name.isEmpty()) {
                errorLabel.setText("Cluster name cannot be empty!");
                return;
            }

            // Create the cluster object
            Cluster newCluster = new Cluster(name);
            newCluster.setStatus("Active");

            // Add the cluster using business logic layer
            clusterManager.addCluster(newCluster);

            mainBorderPane.refreshDefaultPane();

            goBack.run(); // Go back to default center pane
        });

        // Add everything
        getChildren().addAll(
            clusterNameLabel,
            clusterNameTextField,
            createButton
        );
    }

}
