public interface EntityActivity extends EntityAnimation{
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
