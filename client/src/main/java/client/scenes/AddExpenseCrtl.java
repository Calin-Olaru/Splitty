package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AddExpenseCrtl {

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private MenuButton paidByMenu;
    @FXML
    private MenuItem paidByOption1;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private MenuButton currencyMenu;
    @FXML
    private MenuItem currencyOption1;
    @FXML
    private RadioButton splitEvenButton;
    @FXML
    private RadioButton splitButton;
    @FXML
    private AnchorPane splitPersonsPane;
    @FXML
    private CheckBox splitPersonBox1;
    @FXML
    private MenuButton typeMenu;
    @FXML
    private MenuItem typeOption1;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private Button goHomeButton;


    public void goHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}