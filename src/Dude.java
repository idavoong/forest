import processing.core.PImage;

import java.util.List;

abstract class Dude implements Entity, EntityAnimation, EntityActivity, MovableEntity{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private double actionPeriod;
    private double animationPeriod;

    public Dude(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
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
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude;
        if (this.getClass() == DudeFull.class) {
            dude = Factory.createDudeNotFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);
        } else {
            dude = Factory.createDudeFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);
        }
        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        ((EntityAnimation)dude).scheduleActions(scheduler, world, imageStore);
        return true;
    }

    public abstract void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
