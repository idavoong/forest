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
public final class Duck implements MovableEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;

    public Duck(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
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
        Optional<Entity> duckTarget = world.findNearest(position, new ArrayList<>(List.of(DudeNotFull.class, DudeFull.class)));

        if (duckTarget.isPresent()) {
            Point tgtPos = duckTarget.get().getPosition();

            if (moveTo(world, duckTarget.get(), scheduler)) {

                Entity egg = Factory.createEgg("egg" + "_" + duckTarget.get().getId(), tgtPos, 0.2, imageStore.getImageList("egg"));

                world.addEntity(egg);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
    }

    public void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(scheduler, target);
    }
}
