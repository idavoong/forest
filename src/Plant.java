import processing.core.PImage;

import java.util.List;

abstract class Plant implements Entity, EntityAnimation, EntityActivity {
    double TREE_ANIMATION_MAX = 0.600;
    double TREE_ANIMATION_MIN = 0.050;
    double TREE_ACTION_MAX = 1.400;
    double TREE_ACTION_MIN = 1.000;
    int TREE_HEALTH_MAX = 3;
    int TREE_HEALTH_MIN = 1;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;
    private int health;

    public Plant(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
    }

    public void subHealth() {
        this.health--;
    }

    public void addHealth() {
        this.health++;
    }

    public int getHealth() {
        return health;
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

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
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

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }

    private boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getClass() == Sapling.class){
            if (getHealth() >= ((Sapling)this).getHealthLimit()) {
                Entity tree = Factory.createTree(Functions.TREE_KEY + "_" + getId(), getPosition(), Functions.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), Functions.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), Functions.getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));
                world.removeEntity(scheduler, this);
                world.addEntity(tree);
                ((EntityAnimation)tree).scheduleActions(scheduler, world, imageStore);
                return true;
            }
        }

        if (this.getHealth() <= 0) {
            Entity stump = Factory.createStump(Functions.STUMP_KEY + "_" + getId(), getPosition(), imageStore.getImageList(Functions.STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        }
        return false;
    }

}
