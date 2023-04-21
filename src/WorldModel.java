import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    public int numRows;
    public int numCols;
    public Background[][] background;
    public Entity[][] occupancy;
    public Set<Entity> entities;

    public WorldModel() {

    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < numRows && pos.x >= 0 && pos.x < numCols;
    }

    /*
       Assumes that there is no entity currently occupying the
       intended destination cell.
    */
    public void addEntity(Entity entity) {
        if (withinBounds(entity.position)) {
            Functions.setOccupancyCell(this, entity.position, entity);
            entities.add(entity);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        Functions.removeEntityAt(this, entity.position);
    }

    public void tryAddEntity(Entity entity) {
        if (isOccupied(entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }
    
        addEntity(entity);
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            Functions.setOccupancyCell(this, oldPos, null);
            Optional<Entity> occupant = Functions.getOccupant(this, pos);
            occupant.ifPresent(target -> removeEntity(scheduler, target));
            Functions.setOccupancyCell(this, pos, entity);
            entity.position = pos;
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        Functions.parseSaveFile(this, saveFile, imageStore, defaultBackground);
        if(background == null){
            background = new Background[numRows][numCols];
            for (Background[] row : background)
                Arrays.fill(row, defaultBackground);
        }
        if(occupancy == null){
            occupancy = new Entity[numRows][numCols];
            entities = new HashSet<>();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return occupancy[pos.y][pos.x];
    }
}
