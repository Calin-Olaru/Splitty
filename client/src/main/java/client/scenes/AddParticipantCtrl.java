package client.scenes;

import client.utils.ServerUtils;
import commons.Person;
import commons.Currency;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddParticipantCtrl {

    @FXML
    private Button goHomeButton;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private Button addParticipantButton;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField IBAN;
    @FXML
    private TextField BIC;
    @FXML
    Label participantAdded;
    List<Person> participants = new ArrayList<>();

    private MainCtrl mainCtrl;
    private ServerUtils server;

    @Inject
    public AddParticipantCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @FXML
    public void addParticipant(ActionEvent event) {

        if (isValidInput()) {
            try {
                Person p = getParticipant();
                if (participants.contains(p))
                {
                    participantAdded.setText(p.getFirstName() + " was already added");
                    participantAdded.setTextFill(Color.RED);
                    participantAdded.setStyle("-fx-font-weight: bold");
                    return;
                }

                participants.add(p);
                server.addPerson(p);
                System.out.println(p);
            } catch (WebApplicationException e) {

                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return;
            }

            Random random = new Random();
            //generate a random alphanumeric code
            String inviteCode = random.ints(48, 123)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(7)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            participantAdded.setText("Sent invite to " + firstName.getText() + "! Invite code: " + inviteCode);
            participantAdded.setTextFill(Color.BLACK);
            participantAdded.setStyle("-fx-font-weight: light");
        }
        else {
            participantAdded.setText("Please fill out all fields correctly!");
            participantAdded.setTextFill(Color.RED);
            participantAdded.setStyle("-fx-font-weight: bold");
        }

    }

    public Person getParticipant() {
        Person p;
        if (email == null || email.getText().isEmpty())
        {
            p = new Person(firstName.getText(), lastName.getText(), null,
                    IBAN.getText(), BIC.getText(), Currency.EUR, 0.0, null, null);
        }
        else
        {
            p = new Person(firstName.getText(), lastName.getText(), email.getText(),
                    IBAN.getText(), BIC.getText(), Currency.EUR, 0.0, null, null);
        }

        return p;
    }
    public boolean isValidInput() {

        if (firstName == null || firstName.getText().isEmpty())
            return false;

        if (lastName == null || lastName.getText().isEmpty())
            return false;

        if (email != null && !email.getText().isEmpty() &&
        !email.getText().contains("@") && !email.getText().contains(".com"))
            return false;

        if (IBAN == null || IBAN.getText().length() < 34)
            return false;

        if (BIC == null || BIC.getText().isEmpty())
             return false;

        return true;
    }

    public void goHome(ActionEvent event) throws IOException {
        mainCtrl.showHome();
    }


}
