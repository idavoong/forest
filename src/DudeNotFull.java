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
public final class DudeNotFull implements Entity, EntityAnimation, EntityActivity, MovableEntity, Dude {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;

    public DudeNotFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public double getActionPeriod() {
        return actionPeriod;
    };

    public int getImageIndex() {
        return imageIndex;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Entity dude = Factory.createDudeFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            ((EntityAnimation)dude).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        resourceCount += 1;
        ((Plant)target).subHealth();
    }
}
