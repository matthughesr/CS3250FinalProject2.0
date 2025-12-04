import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import javafx.scene.input.ScrollEvent;


public class PodDetailsPane extends ScrollPane{
	private MainBorderPane mainBorderPane;


	public PodDetailsPane(Runnable goBack, Pod pod, MainBorderPane mainBorderPane) {
		this.mainBorderPane = mainBorderPane;

		// Scroll faster
		addEventFilter(ScrollEvent.SCROLL, event -> {
		    double delta = event.getDeltaY() * 6; // Increase multiplier for faster scrolling
		    setVvalue(getVvalue() - delta / getContent().getBoundsInLocal().getHeight());
		    event.consume();
		});

		// Create main container for everything
		VBox mainContainer = new VBox(10);

		// Back button
		Button backButton = new Button("Back");
		backButton.getStyleClass().add("button");
		backButton.setOnAction(e -> goBack.run());
		
//		Button refreshButton = new Button("Refresh");
//		refreshButton.getStyleClass().add("button");
//		refreshButton.setOnAction(event -> {
//	        refreshDefaultPane();
//	    	setCenter(scrollPane);
//        });
//			
		// HBox for back button
		HBox backButtonBox = new HBox(backButton);
		backButtonBox.setAlignment(Pos.TOP_RIGHT);

		// Title label with bold styling
		Label labelPart = new Label("Pod Name: ");
		labelPart.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		// title label part 2: name of pod
		Label namePart = new Label(pod.getName());
		namePart.setStyle("-fx-font-size: 18px; -fx-text-fill: #3366ff;");

		// HBox to center things
		HBox titleBox = new HBox(5, labelPart, namePart); // Add labels to HBox pane
		titleBox.setAlignment(Pos.CENTER); // Center the title horizontally

		// Add back button and title to main container
		mainContainer.getChildren().addAll(backButtonBox, titleBox);

		// Create VBox for labels column
		VBox labelsBox = new VBox(10);

		// Labels for pod specific details
		Label podSectionLabel = new Label("Pod Details");
		podSectionLabel.getStyleClass().add("section-header");
		
		Label namespaceLabel = new Label("Namespace: " + pod.getNamespace());
		namespaceLabel.getStyleClass().add("info-label");

		Label nodeLabel = new Label("Node: " + pod.getNodeName());
		nodeLabel.getStyleClass().add("info-label");

		Label ipLabel = new Label("IP: " + pod.getIp());
		ipLabel.getStyleClass().add("info-label");

		Label cpuLabel = new Label("CPU: " + pod.getCpu());
		cpuLabel.getStyleClass().add("metric-label");

		Label memoryLabel = new Label("Memory: " + pod.getMemory());
		memoryLabel.getStyleClass().add("metric-label");

		Label diskSpaceLabel = new Label("Disk Space: " + pod.getDiskSpace());
		diskSpaceLabel.getStyleClass().add("info-label");
		
		Label podStatusLabel = new Label("Status: " + pod.getStatus());
		podStatusLabel.getStyleClass().add("info-label");
		
		// Button to export the YAML to file for specific pod
		Button exportButton = new Button("Export YAML");
		exportButton.getStyleClass().add("button");
		exportButton.setOnAction(e -> mainBorderPane.getClusterManager().saveYAML(
				pod.getName(), pod.getNamespace(), exportButton.getScene().getWindow())
				);

		// Add all labels to the labels box
		labelsBox.getChildren().addAll(
				podSectionLabel
				, namespaceLabel
				, podStatusLabel
				, ipLabel
				, diskSpaceLabel
				, nodeLabel
				, cpuLabel
				, memoryLabel
				, exportButton);

		Label containerDetailsLabel = new Label("Container Details");
		containerDetailsLabel.getStyleClass().add("section-header");
		labelsBox.getChildren().add(containerDetailsLabel);

		// Get Containers
		List<Container> podContainers =pod.getContainers();

		// Create labels for container info
		for(Container container : podContainers) {
			Label containerName = new Label("Name: " + container.getName());
			containerName.getStyleClass().add("container-label");
			Label containerImage = new Label("Image: " + container.getImage());
			containerImage.getStyleClass().add("container-label");
			containerImage.setWrapText(true);
//			Label containerStatus = new Label("Status: " + container.getStatus());
//			containerStatus.getStyleClass().add("container-label");
			labelsBox.getChildren().addAll(containerName, containerImage);
		}


		
		

		// Line chart to show CPU data
		// Line chart docs "https://docs.oracle.com/javafx/2/charts/line-chart.htm"
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Seconds");
        yAxis.setLabel("CPU (millicores)");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("CPU Usage");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("CPU Usage");
        lineChart.getData().add(series);

        // Set ID for CSS targeting
        lineChart.setId("cpu-chart");


        // Timeline for updating CPU every second
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Fetch live metrics from Kubernetes API
                ApiInterface apiInterface = mainBorderPane.getClusterManager().getApiInterface();

                if (apiInterface != null) {
                    Map<String, String> metrics = apiInterface.fetchPodMetrics(pod.getName(), pod.getNamespace());

                    if (metrics != null && metrics.containsKey("cpu")) {
                        try {
                            double cpuValue = Double.parseDouble(metrics.get("cpu"));
                            cpuLabel.setText(String.format("CPU: %.2f millicores", cpuValue));

                            // x = time in seconds
                            int x = series.getData().size();

                            series.getData().add(new XYChart.Data<>(x, cpuValue));

                            // Limit number of points displayed and re-index
                            if (series.getData().size() > 100) {
                                series.getData().remove(0);
                                // Re-index remaining points to prevent gaps
                                for (int i = 0; i < series.getData().size(); i++) {
                                    series.getData().get(i).setXValue(i);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing CPU value: " + e.getMessage());
                        }
                    } else {
                        System.err.println("No metrics available for pod " + pod.getName());
                    }
                }
            })
        );
        

        // Run CPU timeline forever
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

//        contentBox.getChildren().add(lineChart);

        
        
        
        
        // ========== MEMORY CHART ==========
        final NumberAxis x2Axis = new NumberAxis();
        final NumberAxis y2Axis = new NumberAxis();
        x2Axis.setLabel("Seconds");
        y2Axis.setLabel("Memory (MiB)");

        // Creating the memory chart
        final LineChart<Number,Number> lineChart2 = new LineChart<Number,Number>(x2Axis, y2Axis);
        lineChart2.setTitle("Memory Usage");

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("Memory Usage");
        lineChart2.getData().add(series2);

        // Set ID for CSS targeting
        lineChart2.setId("memory-chart");


        // Timeline for updating memory every second
        Timeline timeline2 = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Fetch live metrics from Kubernetes API
                ApiInterface apiInterface = mainBorderPane.getClusterManager().getApiInterface();

                if (apiInterface != null) {
                    Map<String, String> metrics = apiInterface.fetchPodMetrics(pod.getName(), pod.getNamespace());

                    if (metrics != null && metrics.containsKey("memory")) {
                        try {
                            // Memory is in bytes, convert to MiB (Mebibytes)
                            double memoryBytes = Double.parseDouble(metrics.get("memory"));
                            double memoryMiB = memoryBytes / (1024.0 * 1024.0);
                            memoryLabel.setText(String.format("Memory: %.2f MiB", memoryMiB));

                            // x = time in seconds
                            int x = series2.getData().size();

                            series2.getData().add(new XYChart.Data<>(x, memoryMiB));

                            // Limit number of points displayed and re-index
                            if (series2.getData().size() > 100) {
                                series2.getData().remove(0);
                                // Re-index remaining points to prevent gaps
                                for (int i = 0; i < series2.getData().size(); i++) {
                                    series2.getData().get(i).setXValue(i);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing memory value: " + e.getMessage());
                        }
                    } else {
                        System.err.println("No memory metrics available for pod " + pod.getName());
                    }
                }
            })
        );
        
		// Run memory timeline forever
        timeline2.setCycleCount(Animation.INDEFINITE);
        timeline2.play();

		// Create VBox for charts
		VBox chartVBox = new VBox(20);
		chartVBox.getChildren().addAll(lineChart, lineChart2);

		// Create grid pane for labels and charts
		GridPane gridPane = new GridPane();
		gridPane.setHgap(20); // Horizontal gap between columns
		gridPane.setVgap(10); // Vertical gap between rows

		// Set column constraints: 30% for labels, 70% for charts
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(30);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(70);

		gridPane.getColumnConstraints().addAll(col1, col2);

		gridPane.add(labelsBox, 0, 0); // Add labelsBox to column 0, row 0
		gridPane.add(chartVBox, 1, 0);  // Add chartVBox to column 1, row 0

		// Add GridPane to main container (below back button and title)
		mainContainer.getChildren().add(gridPane);

		// Set the content of the ScrollPane
		setContent(mainContainer);

		// Configure ScrollPane properties
		setFitToWidth(true);
		setPannable(true);
	
	}

}
