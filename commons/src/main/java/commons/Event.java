package commons;


import jakarta.persistence.*;

import java.util.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    private String name;
    private String description;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Tag tag;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Date date;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Person> participants;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Expense> expenses;
    private String inviteCode;

    @SuppressWarnings("Unused")
    protected Event() {
    }

    /**
     * Constructor for event. inviteCode is left out as it is separately generated.
     * @param name String representing the Event's name
     * @param description String representing the Event's description
     * @param tag Tag representing the Event's tag
     * @param date Date representing the Event's date
     * @param participants List of Persons representing the Event's participants
     * @param expenses List of Expenses representing the Event's expenses
     */
    public Event(String name, String description, Tag tag, Date date, List<Person> participants,
                 List<Expense> expenses) {
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.date = date;
        this.participants = Objects.requireNonNullElseGet(participants, ArrayList::new);
        this.expenses = Objects.requireNonNullElseGet(expenses, ArrayList::new);
        this.inviteCode = generateInviteCode();
    }

    /**
     * Getter for an Event's id.
     * @return long representing the Event's id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for an Event's name.
     * @return String representing the Event's name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for an Event's name.
     * @param name String representing the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for an Event's description.
     * @return String representing the Event's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for an Event's description.
     * @param description String representing the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for an Event's tag.
     * @return Tag representing the Event's tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Setter for an Event's tag.
     * @param tag Tag representing the new tag
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * Getter for an Event's date.
     * @return Date representing the Event's date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter for an Event's date.
     * @param date Date representing the Event's new date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter for an Event's participants.
     * @return List of Persons representing the Event's participants
     */
    public List<Person> getParticipants() {
        return participants;
    }

    /**
     * Setter for an Event's participants
     * @param participants List of Persons representing the Event's new List of participants
     */
    public void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

    /**
     * Getter for an Event's expenses. Placeholder.
     * @return List of Expenses representing the Event's logged expenses.
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Setter for an Event's expenses. Placeholder.
     * @param expenses List of Expenses representing the new List of Expenses
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Getter for an Event's unique inviteCode.
     * @return String representing the Event's invite code
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Refresh for an Event's unique inviteCode. Creates a new randomly assigned inviteCode.
     */
    public void refreshInviteCode() {
        this.inviteCode = generateInviteCode();
    }

    /**
     * Determines whether a person is currently attending an event.
     * @param person Person representing the participant to check.
     * @return boolean, true if the Person is currently attending the event, false otherwise.
     */
    public boolean isAttending(Person person) {
        if(person == null) {
            return false;
        }
        return this.participants.contains(person);
    }

    /**
     * Adds a person to the list of participants attending an event. Returns true if successful.
     * @param person Person representing the participant to add to the Event.
     * @return boolean, true if a Person was added successfully, false otherwise.
     */
    public boolean addParticipant(Person person) {
        if(this.isAttending(person)) {
            return false;
        }
        this.getParticipants().add(person);
        return true;
    }

    /**
     * Removes a person from the list of participants attending an event. Returns false if
     * the person does not attend it.
     * @param person Person representing the participant to remove from the Event.
     * @return boolean, true if a Person was successfully removed, false otherwise.
     */
    public boolean removeParticipant(Person person) {
        if(person == null || !this.getParticipants().contains(person)) {
            return false;
        }
        this.getParticipants().remove(person);
        return true;
    }

    /**
     * Adds an expense to the list of expenses for an event. Returns true if successful.
     * Cannot add duplicate Expenses. Placeholder.
     * @param expense Expense representing the Expense to add to the Event.
     * @return boolean, true if the Expense was sucessfully added, false otherwise.
     */
    public boolean addExpense(Expense expense) {
        if(expense == null || this.getExpenses().contains(expense)) {
            return false;
        }
        this.getExpenses().add(expense);
        return true;
    }

    /**
     * Removes an expense from the list of expenses for an event. Returns false if
     * the expense is not within the list. Placeholder.
     * @param expense Expense representing the Expense to remove from the Event.
     * @return boolean, true if the Expense was successfully removed, false otherwise.
     */
    public boolean removeExpense(Expense expense) {
        if(expense == null || !this.getExpenses().contains(expense)) {
            return false;
        }
        this.getExpenses().remove(expense);
        return true;
    }

    /**
     * Generated an invitation code for the event.
     * @return String representing the invite code.
     */
    private static String generateInviteCode() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder string = new StringBuilder();
        Random random = new Random();
        while(string.length() < 8) {
            int i = (int) (random.nextFloat() * characters.length());
            string.append(characters.charAt(i));
        }
        return string.toString();
    }

    /**
     * Equals method for the Event class. Placeholder.
     * @param o Object to compare with.
     * @return boolean, true if o is equal with this.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && Objects.equals(name, event.name) && Objects.equals(description, event.description) && Objects.equals(tag, event.tag) && Objects.equals(date, event.date) && Objects.equals(participants, event.participants) && Objects.equals(expenses, event.expenses) && Objects.equals(inviteCode, event.inviteCode);
    }

    /**
     * Hashcode method for the Event class.
     * @return int representing the hashCode of the respective Event object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tag, date, participants, expenses, inviteCode);
    }
}
