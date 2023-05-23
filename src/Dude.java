public interface Dude extends Entity, EntityAnimation, EntityActivity, MovableEntity{
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

    void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
