package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Tag;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CreateEventCtrl {

    @FXML
    private Button clearBtn;

    @FXML
    private Button createBtn;

    @FXML
    private DatePicker dateField;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea descField;

    @FXML
    private Label descLabel;

    @FXML
    private Button homeBtn;

    @FXML
    private TextArea inviteField;

    @FXML
    private Label inviteLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> tagComboBox;

    @FXML
    private Button addTagBtn;

    private MainCtrl mainCtrl;
    private ServerUtils server;

    private List<Tag> tags;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    @Inject
    public CreateEventCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @FXML
    public void clearFields(ActionEvent event) {
        nameField.clear();
        dateField.getEditor().clear();
        inviteField.clear();
        descField.clear();
    }

    @FXML
    public void createEvent(ActionEvent event) {
        if (!isValidInput()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Event Creation Warning");
            alert.setContentText("Please fill all fields correctly!");
            alert.showAndWait();
            statusLabel.setStyle("-fx-font-weight: bold");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Fill out every field correctly!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Event Creation Alert");
        alert.setContentText("Do you want to create this event?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            //convert LocalDate to date
            LocalDate localDate = dateField.getValue();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Event newEvent = new Event(nameField.getText(), descField.getText(),
                    new ArrayList<>(), date, new ArrayList<>(), new ArrayList<>());

            server.createEvent(newEvent);
            statusLabel.setTextFill(Color.BLACK);
            ClipboardContent inviteCodeClipboard = new ClipboardContent();
            inviteCodeClipboard.putString(newEvent.getInviteCode());
            clipboard.setContent(inviteCodeClipboard);
            statusLabel.setText("Invite code: " + newEvent.getInviteCode() + " (Copied to clipboard!)");

            //We Send the emails;
            String emails = inviteField.getText();
            Scanner mailScanner = new Scanner(emails);
            mailScanner.useDelimiter("\n");
            while (mailScanner.hasNext()){
                String email = mailScanner.nextLine();
                sendMailToParticipants(email, newEvent.getInviteCode(), nameField.getText());
                System.out.println("Email sent to: " + email);
            }
        }
        else{
            nameField.setText(null);
            inviteField.setText(null);
            descField.setText(null);
            dateField.getEditor().clear();
        }
    }

    @FXML
    public void goHome(ActionEvent event) throws IOException {
       mainCtrl.showHome();
    }

    public void addTag() {

    }

    public void sendMailToParticipants(String mail, String inviteCode, String eventName){
        final String username = "use.splitty";
        final String password = "sbfs akue pjrj oiqt";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("use.splitty@gmail.com", "Splitty App"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mail)
            );
            message.setSubject("You're Invited to Join " +  eventName+ " - Use Your Invite Code!");
            message.setText("Hello,\n" +
                    "\n" +
                    "You're invited to join an exclusive event on Splitty, your go-to expenses processing app!\n" +
                    "\n" +
                    "Event Details:\n" +
                    "\n" +
                    "Event Name: " + eventName + "\n" +
                    "To join the event, simply follow these steps:\n" +
                    "\n" +
                    "Open the Splitty app on your device.\n" +
                    "\n" +
                    "On the home screen, locate the option to join an event.\n" +
                    "\n" +
                    "Enter the invite code provided below:\n" +
                    "\n" +
                    "Invite Code: " + inviteCode +"\n" +
                    "\n" +
                    "Once you've entered the invite code, you'll gain instant access to the event and all its exciting activities. Don't miss out on this opportunity to connect with others and enjoy a memorable experience!\n" +
                    "\n" +
                    "If you encounter any issues or have questions, feel free to reach out to our support team at use.splitty@gmail.com. We're here to help ensure you have a seamless experience joining the event.\n" +
                    "\n" +
                    "We look forward to seeing you at the event on Splitty!\n" +
                    "\n" +
                    "Best regards,\n" +
                    "The Splitty Team");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidInput() {

        if (nameField == null || nameField.getText().isEmpty())
            return false;

        if (descField == null || descField.getText().isEmpty())
            return false;

        if (dateField == null || dateField.getEditor().getText().isEmpty())
            return false;

        return true;
    }

    public String generateRandomInviteCode() {
        Random random = new Random();
        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(7)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public void setup() {

        tags = new ArrayList<>();
        tagComboBox.setItems(FXCollections.observableArrayList("Party", "Dinner",
                "Trip"));

    }

}
