public final class Animation implements Action{
    private EntityAnimation entity;
    private int repeatCount;

    public Animation(EntityAnimation entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Functions.createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }
}
