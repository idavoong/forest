public interface EntityActivity extends Entity{
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
