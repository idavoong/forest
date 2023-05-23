public interface MovableEntity extends Entity, EntityAnimation, EntityActivity{
    default Point nextPosition(WorldModel world, Point destPos) {
        Class c;
        if (this.getClass() == Fairy.class) {
            c = House.class;
        } else {
            c = Stump.class;
        }

        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != c) {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != c) {
                newPos = getPosition();
            }
        }

        return newPos;
    }

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(getPosition(), target.getPosition())) {
            moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
