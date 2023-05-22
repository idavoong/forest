public interface EntityAnimation extends Entity {
    double getAnimationPeriod();

    double getActionPeriod();

    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        if (this instanceof EntityActivity) {
            scheduler.scheduleEvent(this, Functions.createActivityAction((EntityActivity) this, world, imageStore), getActionPeriod());
        }
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
    }
}
