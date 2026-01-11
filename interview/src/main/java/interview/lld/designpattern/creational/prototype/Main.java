package interview.lld.designpattern.creational.prototype;

public class Main {
    static void main() {
        EnemyPrototype enemy = new Enemy("FlyingEnemy", 100, 12.0, false, "Laser");

        EnemyPrototype clone = enemy.clone();

        System.out.println(enemy.toString());

        System.out.println(clone.toString());
    }
}

