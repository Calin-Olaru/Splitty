package server.api;

import commons.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.implementations.EventServiceImpl;
import server.services.implementations.PersonServiceImpl;
import server.services.interfaces.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private PersonService personService;
    private ExpenseService expenseService;
    private TagService tagService;
    private DebtService debtService;

    /**
     * Constructor for an EventController
     *
     * @param eventService service class for Event
     * @param personService service class for Person
     * @param expenseService service class for Expense
     * @param tagService service class for Tag
     */
    @Autowired
    public EventController(EventServiceImpl eventService, PersonServiceImpl personService, ExpenseService expenseService, TagService tagService,
                           DebtService debtService) {
        this.eventService = eventService;
        this.personService = personService;
        this.expenseService = expenseService;
        this.tagService = tagService;
        this.debtService = debtService;
    }

    /**
     * Creates and saves an Event to the database with the specified fields.
     * @param name name to use for the new Event
     * @param description description to use for the new Event
     * @param tagIDs List of Tags IDs to use for the new Event
     * @param date Date the Event will take place on
     * @param participantIDs List of Person IDs of Persons that will attend the Event
     * @param expenseIDs List of Expense IDs of Expenses associated with the Event
     * @return the created Event Object
     */
    @PostMapping("/")
    public Event createEvent(@RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("tags") List<Long> tagIDs,
                             @RequestParam("date") Date date,
                             @RequestParam("participantIDs") List<Long> participantIDs,
                             @RequestParam("expenseIDs") List<Long> expenseIDs) {
        if(description == null || date == null || name == null) {
            System.out.println("Process aborted, null arguments received.");
            return null;
        }
        ArrayList<Tag> tags =  new ArrayList<>();
        ArrayList<Person> participants = new ArrayList<>();
        ArrayList<Expense> expenses = new ArrayList<>();
        Event event = null;
        boolean validEntry = true;
        try {
            for(Long tID : tagIDs) {
                tagService.getReferenceById(tID);
                tags.add(tagService.getReferenceById(tID));
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Invalid ID, no Tag found.");
            validEntry = false;
        }
        try {
            for(Long pID : participantIDs) {
                personService.getReferenceById(pID);
                participants.add(personService.getReferenceById(pID));
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Invalid ID, no Participant found.");
            validEntry = false;
        }
        try {
            for(Long eID : expenseIDs) {
                expenseService.getReferenceById(eID);
                expenses.add(expenseService.getReferenceById(eID));
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Invalid ID, no Expense found.");
            validEntry = false;
        }
        if(!validEntry) {
            return null;
        }
        event = new Event(name, description, tags, date, participants, expenses);
        eventService.save(event);
        return event;
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<Event> add(@RequestBody Event event) {

        if (event == null || isNullOrEmpty(event.getName()) ||
        isNullOrEmpty(event.getDescription()) || event.getDate() == null)
            return ResponseEntity.badRequest().build();

        Event saved = eventService.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("{id}/newExpense")
    public ResponseEntity<Event> addExpense(@PathVariable("id") long eventId, @RequestBody Expense expense) {
        Event event = getById(eventId).getBody();
        if (event == null)
            return ResponseEntity.badRequest().build();
        List<Debt> debts = expense.getDebtList();
        expense.setDebtList(new ArrayList<>());
        expenseService.add(expense);


        for (Debt debt : debts) {
            debt.setExpense(expense); // Set the Expense on each Debt entity
            debtService.save(debt); // Save each Debt entity
        }
        expense.setDebtList(debts);
        expenseService.add(expense);
        event.getExpenses().add(expense);
        return ResponseEntity.ok(eventService.save(event));
    }

    @PutMapping("/persist")
    public ResponseEntity<Event> persistEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.save(event));
    }

    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {

        if (id < 0 || !eventService.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(eventService.findById(id).get());
    }

    @GetMapping("/{id}/expenses")
    public ResponseEntity<List<Expense>> getExpenses(@PathVariable("id") long id) {
        return null;
    }

    @GetMapping("event/{id}")
    public Event getEventById(@PathVariable("id") long id) {
        if (id < 0 || !eventService.existsById(id)) {
            return null;
        }
        return eventService.findById(id).get();
    }

    /**
     * Gets Event by inviteCode
     * @param inviteCode the inviteCode
     * @return the ResponseEntity
     */
    @GetMapping("/inviteCode/{inviteCode}")
    public ResponseEntity<Event> getEventByInviteCode(@PathVariable("inviteCode") String inviteCode){
        Event event = eventService.findByInviteCode(inviteCode);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{id}/totalExpenses")
    public ResponseEntity<Double> getTotalExpenses(@PathVariable("id") long id) {
        return eventService.getExpensesSum(id);
    }
    /**
     * Deletes the Event by id
     * @param id the id of the event
     * @return ResponseEntity that tells that it worked/didn't work
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Event> deleteById(@PathVariable("id") long id) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Event deletedEvent = eventService.findById(id).get();
        eventService.deleteById(id);
        return ResponseEntity.ok(deletedEvent);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }


}

