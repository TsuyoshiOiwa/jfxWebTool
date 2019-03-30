package oiwa.tsuyoshi.jfxwebtool;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oiwa.tsuyoshi.jfxwebtool.controller.RequestFormController;

public class HelloFx extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("oiwa.tsuyoshi.jfxwebtool.controller.request_form");
        Parent root = FXMLLoader.load(RequestFormController.class.getResource("request_form.fxml"), bundle);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}