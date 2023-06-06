import java.util.Objects;

public class Node {
    public Point point;
    public int g;
    public int h;
    public int f;
    public Node prior;

    public Node(Point point) {
        this.point = point;
    }

    public Node(Point point, int g, int h, int f, Node prior) {
        this.point = point;
        this.g = g;
        this.h = h;
        this.f = f;
        this.prior = prior;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(point, node.point);
    }

    public int distance(Node n) {
        int dis = 0;
        dis += Math.abs(this.point.x - n.point.x);
        dis += Math.abs(this.point.y - n.point.y);
        return dis;
    }
}
