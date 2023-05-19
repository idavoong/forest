public interface EntityActions extends Entity {
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

    double getAnimationPeriod();
}
