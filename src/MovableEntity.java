import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface MovableEntity extends EntityActivity{
    default Point nextPosition(WorldModel world, Point destPos) {
        //PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();
        //exclude the start / end, and be in ascending order

        Class c;
        if (this.getClass() == Fairy.class) {
            c = House.class;
        } else {
            c = Stump.class;
        }

        Predicate<Point> canPassThrough = curPoint -> {
            if (world.withinBounds(curPoint) && ((!world.isOccupied(curPoint) || world.getOccupancyCell(curPoint).getClass() == c))){
                return true;
            }
            return false;
        };

        BiPredicate<Point, Point> withinReach = (curPoint, destPoint) -> {
            if (Point.adjacent(curPoint, destPoint)) {
                return true;
            }
            return false;
        };

        List<Point> path = strat.computePath(this.getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()) {
            return this.getPosition();
        }
        return path.get(0);
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

    void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
