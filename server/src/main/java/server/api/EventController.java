package server.api;

import commons.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.implementations.EventServiceImpl;
import server.services.implementations.PersonServiceImpl;
import server.services.interfaces.EventService;
import server.services.interfaces.ExpenseService;
import server.services.interfaces.PersonService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private PersonService personService;
    private ExpenseService expenseService;
    // private TagRepository tagRep

    /**
     * Constructor for an EventController
     *
     * @param eventService service class for Event
     * @param personService service class for Person
     * @param expenseService service class for Expense
     */
    public EventController(EventServiceImpl eventService, PersonServiceImpl personService, ExpenseService expenseService) {
        this.eventService = eventService;
        this.personService = personService;
        this.expenseService = expenseService;
    }

    /**
     * Creates and saves an Event to the database with the specified fields.
     * @param name name to use for the new Event
     * @param description description to use for the new Event
     * @param tags List of Tags to use for the new Event
     * @param date Date the Event will take place on
     * @param participantIDs List of Person IDs of Persons that will attend the Event
     * @param expenseIDs List of Expense IDs of Expenses associated with the Event
     * @return the created Event Object
     */
    @PostMapping("/")
    public Event createEvent(@RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("tags") List<Tag> tags,
                             @RequestParam("date") Date date,
                             @RequestParam("participantIDs") List<Long> participantIDs,
                             @RequestParam("expenseIDs") List<Long> expenseIDs) {
        if(description == null || date == null || name == null || tags == null) {
            System.out.println("Process aborted, null arguments received.");
            return null;
        }
        ArrayList<Person> participants = new ArrayList<>();
        ArrayList<Expense> expenses = new ArrayList<>();
        Event event = null;
        try {
            for(Long pID : participantIDs) {
                personService.getReferenceById(pID);
                participants.add(personService.getReferenceById(pID));
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Invalid ID, no Participant found.");
        }
        try {
            for(Long eID : expenseIDs) {
                expenseService.getReferenceById(eID);
                expenses.add(expenseService.getReferenceById(eID));
            }
        }
        catch (EntityNotFoundException e) {
            System.out.println("Invalid ID, no Expense found.");
        }

        event = new Event(name, description, tags, date, participants, expenses);
        eventService.save(event);
        return event;
    }

    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        System.out.println("Find people...");
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {

        if (id < 0 || !eventService.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(eventService.findById(id));
    }


    @GetMapping("event/{id}")
    public Event getEventById(@PathVariable("id") long id) {
        if (id < 0 || !eventService.existsById(id)) {
            return null;
        }
        return eventService.findById(id);
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
        Event deletedEvent = eventService.findById(id);
        eventService.deleteById(id);
        return ResponseEntity.ok(deletedEvent);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }


}

