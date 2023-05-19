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
public final class Sapling implements Entity, EntityAnimation, EntityActivity, Plant {
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;

    public void setHealth() {
        health--;
    }
    private EntityKind kind;
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;
    private int health;
    private int healthLimit;

    public Sapling(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
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

    public EntityKind getKind() {
        return kind;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public int getHealth() {
        return health;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        health++;
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
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

    private boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transformSapling(world, scheduler, imageStore);
    }

    private boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + id, position, imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (health >= healthLimit) {
            Entity tree = Functions.createTree(Functions.TREE_KEY + "_" + id, position, Functions.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), Functions.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), Functions.getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            ((EntityAnimation)tree).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
}
