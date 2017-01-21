package com.github.shaigem.linkgem;

import com.airhacks.afterburner.injection.Injector;
import com.github.shaigem.linkgem.ui.events.SaveAllEvent;
import com.github.shaigem.linkgem.ui.main.MainWindowView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

import static org.sejda.eventstudio.StaticStudio.eventStudio;

/**
 * Created on 2016-12-21.
 *
 * @author Ronnie Tran
 */
public class LinkGemApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new MainWindowView().getView());
        primaryStage.setScene(scene);
        primaryStage.setTitle("LinkGem");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> showThanksDialog());
    }

    /**
     * Create a popup dialog thanking the user for using the software and also ask them if they would like to save any changes.
     */
    private void showThanksDialog() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanks");
        alert.setHeaderText("Thanks");
        alert.setContentText("Thanks for using LinkGem! Before you go, would you like to " +
                "save any changes that you may have made?");
        alert.setGraphic(GlyphsDude.createIcon(MaterialDesignIcon.CONTENT_SAVE_ALL, "2.5em"));
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        final Optional<ButtonType> buttonType = alert.showAndWait();
        buttonType.ifPresent(type -> {
            if (type == ButtonType.YES) {
                eventStudio().broadcast(new SaveAllEvent());
            } else {
                Platform.exit();
            }
        });

    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    /**
     * Launches a JavaFX application and calls the start method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
