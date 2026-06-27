import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}

abstract class GameCharacter {
    private String name;
    private int hp;
    private int maxHp;

    public GameCharacter(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int damage) {
        hp -= damage;

        if (hp < 0) {
            hp = 0;
        }
    }

    public void heal(int amount) {
        hp += amount;

        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public void setHp(int hp) {
        this.hp = hp;

        if (this.hp > maxHp) {
            this.hp = maxHp;
        }

        if (this.hp < 0) {
            this.hp = 0;
        }
    }
}

class Defender extends GameCharacter {
    private int resourcePoint;
    private Random random;
    private final int vitaminCost;
    private final int vitaminHeal;

    public Defender() {
        super("Defender", 100);
        this.resourcePoint = 0;
        this.random = new Random();
        this.vitaminCost = 20;
        this.vitaminHeal = 30;
    }

    public int getResourcePoint() {
        return resourcePoint;
    }

    public int getVitaminCost() {
        return vitaminCost;
    }

    public int getVitaminHeal() {
        return vitaminHeal;
    }

    public void addResourcePoint(int point) {
        resourcePoint += point;
    }

    public void useResourcePoint(int point) {
        resourcePoint -= point;

        if (resourcePoint < 0) {
            resourcePoint = 0;
        }
    }

    public int attack(Flea flea) {
        int damage = random.nextInt(16) + 15;
        int selfDamage = random.nextInt(6) + 5;

        flea.takeDamage(damage);
        takeDamage(selfDamage);

        return damage;
    }

    public int train() {
        int healAmount = random.nextInt(16) + 15;
        int beforeHp = getHp();

        heal(healAmount);

        return getHp() - beforeHp;
    }

    public int buyVitamin() {
        if (getHp() >= getMaxHp()) {
            return -2;
        }

        if (resourcePoint < vitaminCost) {
            return -1;
        }

        int beforeHp = getHp();

        useResourcePoint(vitaminCost);
        heal(vitaminHeal);

        return getHp() - beforeHp;
    }
}

class Flea extends GameCharacter {
    private Random random;

    public Flea() {
        super("Flea", 50);
        this.random = new Random();
    }

    public int attack(Defender defender) {
        int damage = random.nextInt(16) + 10;
        defender.takeDamage(damage);
        return damage;
    }

    public void respawn() {
        setHp(getMaxHp());
    }
}

class Game {
    private Scanner scanner;
    private Defender defender;
    private Flea flea;
    private int currentTime;
    private int maxTime;
    private int fleaAttackInterval;
    private int nextFleaAttackTime;

    public Game() {
        scanner = new Scanner(System.in);
        defender = new Defender();
        flea = new Flea();
        currentTime = 0;
        maxTime = 300;
        fleaAttackInterval = 20;
        nextFleaAttackTime = 20;
    }

    public void start() {
        System.out.println("          THE FLEA DEFENDER");
        System.out.println("Bertahan hidup selama 300 detik.");
        System.out.println("Defender memiliki HP awal 100 dan RP awal 0.");
        System.out.println("Flea akan menyerang otomatis setiap 20 detik.");
        System.out.println("Vitamin membutuhkan " + defender.getVitaminCost() + " RP dan memulihkan maksimal " + defender.getVitaminHeal() + " HP.");

        while (defender.isAlive() && currentTime < maxTime) {
            showStatus();
            showMenu();

            int choice = inputChoice();
            processChoice(choice);
        }

        if (defender.isAlive() && currentTime >= maxTime) {
            showSummary(true);
        } else {
            showSummary(false);
        }
    }

    private void showStatus() {
        System.out.println();
        System.out.println("              STATUS               ");
        System.out.println("Waktu          : " + currentTime + " / " + maxTime + " detik");
        System.out.println("HP Defender    : " + defender.getHp() + " / " + defender.getMaxHp());
        System.out.println("RP Defender    : " + defender.getResourcePoint());
        System.out.println("HP Flea        : " + flea.getHp() + " / " + flea.getMaxHp());
    }

    private void showMenu() {
        System.out.println("1. Serang Flea");
        System.out.println("2. Latihan untuk memulihkan HP");
        System.out.println("3. Beli Vitamin");
        System.out.println("4. Lewati Waktu");
        System.out.print("Pilih aksi: ");
    }

    private int inputChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Input harus berupa angka. Pilih aksi: ");
            scanner.next();
        }

        return scanner.nextInt();
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                attackFlea();
                advanceTime(10);
                break;

            case 2:
                trainDefender();
                advanceTime(10);
                break;

            case 3:
                buyVitamin();
                break;

            case 4:
                System.out.println();
                System.out.println("Defender menunggu dan mengamati sekitar.");
                advanceTime(10);
                break;

            default:
                System.out.println();
                System.out.println("Pilihan tidak valid.");
                break;
        }
    }

    private void attackFlea() {
        int damage = defender.attack(flea);

        System.out.println();
        System.out.println("Defender menyerang Flea dan memberikan damage sebesar " + damage + ".");
        System.out.println("Karena risiko pertarungan, Defender kehilangan sebagian HP.");

        if (!flea.isAlive()) {
            int reward = 20;
            defender.addResourcePoint(reward);

            System.out.println("Flea berhasil dikalahkan.");
            System.out.println("Defender mendapatkan " + reward + " RP.");
            System.out.println("Flea baru muncul kembali.");

            flea.respawn();
        }
    }

    private void trainDefender() {
        int healAmount = defender.train();

        System.out.println();
        System.out.println("Defender melakukan latihan.");

        if (healAmount > 0) {
            System.out.println("HP Defender pulih sebesar " + healAmount + ".");
        } else {
            System.out.println("HP Defender sudah penuh.");
        }

        System.out.println("Latihan membutuhkan waktu 10 detik.");
    }

    private void buyVitamin() {
        int result = defender.buyVitamin();

        System.out.println();

        if (result == -2) {
            System.out.println("HP Defender sudah penuh.");
            System.out.println("Vitamin tidak digunakan dan RP tidak berkurang.");
        } else if (result == -1) {
            System.out.println("RP tidak cukup untuk membeli vitamin.");
            System.out.println("Vitamin membutuhkan " + defender.getVitaminCost() + " RP.");
            System.out.println("RP kamu saat ini hanya " + defender.getResourcePoint() + ".");
        } else {
            System.out.println("Defender membeli vitamin.");
            System.out.println("HP Defender pulih sebesar " + result + ".");
            System.out.println("RP berkurang sebesar " + defender.getVitaminCost() + ".");
        }
    }

    private void advanceTime(int seconds) {
        int targetTime = currentTime + seconds;

        if (targetTime > maxTime) {
            targetTime = maxTime;
        }

        while (nextFleaAttackTime <= targetTime && defender.isAlive()) {
            currentTime = nextFleaAttackTime;
            checkFleaAttack();
            nextFleaAttackTime += fleaAttackInterval;

            if (!defender.isAlive()) {
                return;
            }
        }

        currentTime = targetTime;
    }

    private void checkFleaAttack() {
        int damage = flea.attack(defender);

        System.out.println();
        System.out.println("Flea menyerang otomatis pada detik ke-" + currentTime + ".");
        System.out.println("Defender terkena damage sebesar " + damage + ".");

        if (!defender.isAlive()) {
            System.out.println("HP Defender habis.");
        }
    }

    private void showSummary(boolean win) {
        System.out.println();
        System.out.println("              RINGKASAN");

        if (win) {
            System.out.println("Status Akhir : BERHASIL BERTAHAN");
        } else {
            System.out.println("Status Akhir : GAME OVER");
        }

        System.out.println("Waktu Akhir  : " + currentTime + " detik");
        System.out.println("Sisa HP      : " + defender.getHp());
        System.out.println("Total RP     : " + defender.getResourcePoint());
    }
}
