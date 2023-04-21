import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler {
    public PriorityQueue<Event> eventQueue;
    public Map<Entity, List<Event>> pendingEvents;
    public double currentTime;

    public EventScheduler() {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.currentTime = 0;
    }

    public void scheduleEvent(Entity entity, Action action, double afterPeriod) {
        double time = currentTime + afterPeriod;
    
        Event event = new Event(action, time, entity);
    
        eventQueue.add(event);
    
        // update list of pending events for the given entity
        List<Event> pending = pendingEvents.getOrDefault(entity, new LinkedList<>());
        pending.add(event);
        pendingEvents.put(entity, pending);
    }

    public void unscheduleAllEvents(Entity entity) {
        List<Event> pending = pendingEvents.remove(entity);
    
        if (pending != null) {
            for (Event event : pending) {
                eventQueue.remove(event);
            }
        }
    }

    public void updateOnTime(double time) {
        double stopTime = currentTime + time;
        while (!eventQueue.isEmpty() && eventQueue.peek().time <= stopTime) {
            Event next = eventQueue.poll();
            removePendingEvent(next);
            currentTime = next.time;
            next.action.executeAction(this);
        }
        currentTime = stopTime;
    }

    public void removePendingEvent(Event event) {
        List<Event> pending = pendingEvents.get(event.entity);
    
        if (pending != null) {
            pending.remove(event);
        }
    }
}
