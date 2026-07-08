package simulator.ui.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import simulator.ui.Navigator;

// Landing screen: project title, summary, and primary entry actions.
public class LandingView implements View {
    private final Navigator navigator;
    private final VBox root;

    public LandingView(Navigator navigator) {
        this.navigator = navigator;
        this.root = build();
    }

    private VBox build() {
        Label title = new Label("Service Department Optimization Simulator");
        title.getStyleClass().add("app-title");

        Label subtitle =
                new Label(
                        "Discrete-event simulation of an automotive dealership service department. "
                                + "Model staffing, queues, and parts flow to expose bottlenecks.");
        subtitle.getStyleClass().add("app-subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(560);
        subtitle.setAlignment(Pos.CENTER);

        Button newRun = new Button("New Simulation");
        newRun.getStyleClass().addAll("btn", "primary");
        newRun.setOnAction(e -> navigator.navigateTo(new ConfigView(navigator)));

        Button exit = new Button("Exit");
        exit.getStyleClass().add("btn");
        exit.setOnAction(e -> navigator.exit());

        HBox actions = new HBox(16, newRun, exit);
        actions.setAlignment(Pos.CENTER);

        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        Label footer = new Label("CS 4632 W01 - Sankalp Amaravadi");
        footer.getStyleClass().add("app-footer");

        VBox layout = new VBox(20, topSpacer, title, subtitle, actions, bottomSpacer, footer);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().addAll("app-root", "landing-root");
        return layout;
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
