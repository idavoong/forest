public final class Activity implements Action{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        switch (entity.getKind()) {
            case SAPLING:
                entity.executeSaplingActivity(world, imageStore, scheduler);
                break;
            case TREE:
                entity.executeTreeActivity(world, imageStore, scheduler);
                break;
            case FAIRY:
                entity.executeFairyActivity(world, imageStore, scheduler);
                break;
            case DUDE_NOT_FULL:
                entity.executeDudeNotFullActivity(world, imageStore, scheduler);
                break;
            case DUDE_FULL:
                entity.executeDudeFullActivity(world, imageStore, scheduler);
                break;
            default:
                throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", entity.getKind()));
        }
    }
}
