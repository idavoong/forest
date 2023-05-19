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
        if (entity.getClass().equals(Sapling.class)) {
            ((Sapling) entity).executeSaplingActivity(world, imageStore, scheduler);
        } else if (entity.getClass().equals(Tree.class)) {
            ((Tree) entity).executeTreeActivity(world, imageStore, scheduler);
        } else if (entity.getClass().equals(Fairy.class)) {
            ((Fairy) entity).executeFairyActivity(world, imageStore, scheduler);
        } else if (entity.getClass().equals(DudeNotFull.class)) {
            ((DudeNotFull) entity).executeDudeNotFullActivity(world, imageStore, scheduler);
        } else if (entity.getClass().equals(DudeFull.class)) {
            ((DudeFull) entity).executeDudeFullActivity(world, imageStore, scheduler);
        } else {
            throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", entity.getKind()));
        }
    }
}
