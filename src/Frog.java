import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import processing.core.PImage;

public final class Frog implements MovableEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;

    public Frog(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
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
        Optional<Entity> frogTarget = world.findNearest(position, new ArrayList<>(List.of(Tree.class, Sapling.class, Stump.class)));

        if (frogTarget.isPresent()) {
            Point tgtPos = frogTarget.get().getPosition();

            if (moveTo(world, frogTarget.get(), scheduler)) {

                Entity flower = Factory.createFlower(Parse.SAPLING_KEY + "_" + frogTarget.get().getId(), tgtPos, 0.5, imageStore.getImageList("flower"));

                world.addEntity(flower);
                ((EntityAnimation)flower).scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
    }

    public void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(scheduler, target);
    }
}
