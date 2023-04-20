import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public double actionPeriod;
    public double animationPeriod;
    public int health;
    public int healthLimit;

    public Entity(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        health++;
        if (!Functions.transformPlant(this, world, scheduler, imageStore)) {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
    
        if (!Functions.transformPlant(this, world, scheduler, imageStore)) {
    
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, position, new ArrayList<>(List.of(EntityKind.STUMP)));
    
        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;
    
            if (Functions.moveToFairy(this, world, fairyTarget.get(), scheduler)) {
    
                Entity sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, Functions.getImageList(imageStore, Functions.SAPLING_KEY), 0);
    
                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }
    
        Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
    }

    public void executeDudeNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));
    
        if (target.isEmpty() || !Functions.moveToNotFull(this, world, target.get(), scheduler) || !transformNotFull(world, scheduler, imageStore)) {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void executeDudeFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, position, new ArrayList<>(List.of(EntityKind.HOUSE)));
    
        if (fullTarget.isPresent() && Functions.moveToFull(this, world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Entity dude = Functions.createDudeFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);
    
            Functions.removeEntity(world, scheduler, this);
            Functions.unscheduleAllEvents(scheduler, this);
    
            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);
    
            return true;
        }
    
        return false;
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Functions.createDudeNotFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);
    
        Functions.removeEntity(world, scheduler, this);
    
        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);
    
        if (horiz == 0 || Functions.isOccupied(world, newPos)  && Functions.getOccupancyCell(world, newPos).kind != EntityKind.HOUSE) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);
    
            if (vert == 0 || Functions.isOccupied(world, newPos)  && Functions.getOccupancyCell(world, newPos).kind != EntityKind.HOUSE) {
                newPos = position;
            }
        }
    
        return newPos;
    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);
    
        if (horiz == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);
    
            if (vert == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
                newPos = position;
            }
        }
    
        return newPos;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (kind) {
            case DUDE_FULL:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            case DUDE_NOT_FULL:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            case OBSTACLE:
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            case FAIRY:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            case SAPLING:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            case TREE:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), Functions.getAnimationPeriod(this));
                break;
    
            default:
        }
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }
}
