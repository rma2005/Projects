import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game {

    private final Stage stage;
    private Pane gamePane;
    private Label scoreLabel;
    private Label outcome;
    private int score;
    private Timeline targetTimeline;
    private final String difficulty;
    private int counter;
    private Button retry;
    private Button goBack;
    private final Scene mainMenu;

    public Game(Stage stage, Scene mainMenu, String difficulty) {
        this.stage = stage;
        this.score = 0;
        this.difficulty = difficulty;
        this.mainMenu = mainMenu;
        this.setupGameScene();
        this.startGame(this.difficulty);
    }

    private void setupGameScene() {
        this.gamePane = new Pane();
        this.scoreLabel = new Label("Score: 0");
        this.outcome = new Label();
        this.retry = new Button();
        this.goBack = new Button();
        this.scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        this.gamePane.getChildren().addAll(this.scoreLabel, this.outcome);
        this.scoreLabel.setLayoutX(10);
        this.scoreLabel.setLayoutY(10);

        Scene gameScene = new Scene(this.gamePane, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

        this.gamePane.setStyle("-fx-background-color: black;");  // Set background to black
        this.stage.setScene(gameScene);
    }

    // Start the game based on difficulty
    private void startGame(String difficulty) {
        int targetSize;
        int spawnRate;  // How often a new target appears (milliseconds)
        int targetDuration;  // How long a target stays on screen (milliseconds)

        switch (difficulty) {
            case "easy":
                targetSize = Constants.TARGET_SIZE_EASY;
                spawnRate = Constants.SPAWN_RATE_EASY;
                targetDuration = Constants.DURATION_EASY;
                break;
            case "medium":
                targetSize = Constants.TARGET_SIZE_MEDIUM;
                spawnRate = Constants.SPAWN_RATE_MEDIUM;
                targetDuration = Constants.DURATION_MEDIUM;
                break;
            case "hard":
                targetSize = Constants.TARGET_SIZE_HARD;
                spawnRate = Constants.SPAWN_RATE_HARD;
                targetDuration = Constants.DURATION_HARD;
                break;
            default:
                String[] customSettings = this.difficulty.split(",");
                targetSize = Integer.parseInt(customSettings[0]);
                spawnRate = Integer.parseInt(customSettings[1]);
                targetDuration = Integer.parseInt(customSettings[2]);
                break;
        }

        // Create a Timeline for spawning and removing targets
        this.targetTimeline = new Timeline(new KeyFrame(Duration.millis(spawnRate), event -> {
            createTarget(targetSize, targetDuration);
        }));
        this.targetTimeline.setCycleCount(Timeline.INDEFINITE);
        this.targetTimeline.play();
    }

    // Create a target that appears on the screen
    private void createTarget(int size, int duration) {
        Circle target = new Circle(size);
        target.setFill(Color.RED);

        // Randomize target's position
        double xPos = Math.random() * (gamePane.getWidth() - size * 2) + size;
        double yPos = Math.random() * (gamePane.getHeight() - size * 2) + size;
        target.setCenterX(xPos);
        target.setCenterY(yPos);

        if(this.counter < 10) {
            gamePane.getChildren().add(target);
        }

        // Set an event handler for target clicks
        target.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.gamePane.getChildren().remove(target);  // Remove the target
                this.score++;
                this.scoreLabel.setText("Score: " + this.score);
            }
        });

        // Remove the target after the specified duration if not clicked
        Timeline removeTargetTimeline = new Timeline(new KeyFrame(Duration.millis(duration), event -> {
            gamePane.getChildren().remove(target);
        }));
        removeTargetTimeline.setCycleCount(1);
        removeTargetTimeline.play();
        this.counter++;
        if(this.counter > 10) {
            this.endGame();
        }
    }



    private void endGame(){
        this.targetTimeline.stop();
        this.gamePane.getChildren().remove(this.scoreLabel);
        this.outcome.setFont(Font.font("Calibri", 50));
        this.outcome.setLayoutY(250);
        this.outcome.setLayoutX(200);
        this.outcome.setText("SCORE: " + this.score);
        this.outcome.toFront();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    this.outcome.setTextFill(Color.RED);
                }),
                new KeyFrame(Duration.seconds(1), event -> {
                    this.outcome.setTextFill(Color.WHITE);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        this.gamePane.getChildren().addAll(this.retry, this.goBack);
        this.retry.setText("Go Again?");
        this.retry.setLayoutX(270);
        this.retry.setLayoutY(350);

        this.goBack.setText("Go Back");
        this.goBack.setLayoutX(275);
        this.goBack.setLayoutY(400);

        this.retry.setOnAction(event -> this.handleRetry());
        this.goBack.setOnAction(event -> this.stage.setScene(this.mainMenu));
    }

    private void handleRetry() {
        this.counter = 0;
        this.score = 0;
        new Game(this.stage, this.mainMenu, this.difficulty);
    }
}



