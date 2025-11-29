import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/* 
 * This code is adapted from the code written in class by Professor Rhodes
 * */
public class ProgressWindow extends Stage{
	private ProgressBar progressBar = new ProgressBar(0);
	private Label percentLabel = new Label("0%");
	private Label titleLabel = new Label("Progressing...");
	
	public ProgressWindow(Stage owner) {
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		initStyle(StageStyle.TRANSPARENT);
		setHeight(340);
		setWidth(130);
		
		HBox titleBar = new HBox(titleLabel);
		HBox progressBox = new HBox(10, progressBar, percentLabel);
		VBox root = new VBox(12, titleBar, progressBox);
		
		Scene scene = new Scene (root);
		setScene(scene);
	}
	
	public void update(double p) {
		Platform.runLater(() -> {
			progressBar.setProgress(p);
			percentLabel.setText((int)(p*100) + "%");
		});
	}
	
	public void dispose() {
		Platform.runLater(this::close);
	}

}
