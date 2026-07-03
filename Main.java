import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}

// Class Induk Abstrak
abstract class GameCharacter {
    private String name;
    private int hp;
    private int maxHp;

    public GameCharacter(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean isAlive() { return hp > 0; }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp > maxHp) this.hp = maxHp;
        if (this.hp < 0) this.hp = 0;
    }

    // Penerapan Polymorphism: Method abstrak untuk menyerang
    public abstract int attack(GameCharacter target);
}

// Class Defender
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

    public int getResourcePoint() { return resourcePoint; }
    public int getVitaminCost() { return vitaminCost; }
    public int getVitaminHeal() { return vitaminHeal; }
    public void addResourcePoint(int point) { resourcePoint += point; }

    public void useResourcePoint(int point) {
        resourcePoint -= point;
        if (resourcePoint < 0) resourcePoint = 0;
    }

    @Override
    public int attack(GameCharacter target) {
        int damage = random.nextInt(16) + 15; // 15 - 30
        int selfDamage = random.nextInt(6) + 5;   // 5 - 10

        target.takeDamage(damage);
        this.takeDamage(selfDamage);

        return damage;
    }

    public int train() {
        int healAmount = random.nextInt(16) + 15; // 15 - 30
        int beforeHp = getHp();
        heal(healAmount);
        return getHp() - beforeHp;
    }

    public int buyVitamin() {
        if (getHp() >= getMaxHp()) return -2;
        if (resourcePoint < vitaminCost) return -1;

        int beforeHp = getHp();
        useResourcePoint(vitaminCost);
        heal(vitaminHeal);
        return getHp() - beforeHp;
    }
}

// Class Flea
class Flea extends GameCharacter {
    private Random random;

    public Flea() {
        super("Flea", 50);
        this.random = new Random();
    }

    @Override
    public int attack(GameCharacter target) {
        int damage = random.nextInt(16) + 10; // 10 - 25
        target.takeDamage(damage);
        return damage;
    }

    public void respawn() {
        setHp(getMaxHp());
    }
}

// Class Utama Game Engine
class Game {
    private Scanner scanner;
    private Defender defender;
    private Flea flea;
    private int currentTime;
    private int maxTime;
    private int fleaAttackInterval;
    private int nextFleaAttackTime;
    
    // Fitur Tambahan Balancing: Mencegah spam latihan berturut-turut
    private boolean canTrain; 

    public Game() {
        scanner = new Scanner(System.in);
        defender = new Defender();
        flea = new Flea();
        currentTime = 0;
        maxTime = 300;
        fleaAttackInterval = 20;
        nextFleaAttackTime = 20;
        canTrain = true;
    }

    public void start() {
        System.out.println("==================================================");
        System.out.println("               THE FLEA DEFENDER");
        System.out.println("==================================================");
        System.out.println("Bertahan hidup selama 300 detik.");
        System.out.println("Defender memiliki HP awal 100 dan RP awal 0.");
        System.out.println("Flea akan menyerang otomatis setiap 20 detik.");
        System.out.println("Vitamin membutuhkan " + defender.getVitaminCost() + " RP (Pulih " + defender.getVitaminHeal() + " HP).");
        System.out.println("Latihan tidak bisa dilakukan 2 kali berturut-turut.");
        System.out.println("==================================================");

        while (defender.isAlive() && currentTime < maxTime) {
            showStatus();
            showMenu();
            int choice = inputChoice();
            processChoice(choice);
        }

        showSummary(defender.isAlive() && currentTime >= maxTime);
    }

    private void showStatus() {
        System.out.println("\n----------------- STATUS GAME -----------------");
        System.out.println("Waktu          : " + currentTime + " / " + maxTime + " detik");
        System.out.println("HP Defender    : " + defender.getHp() + " / " + defender.getMaxHp());
        System.out.println("RP Defender    : " + defender.getResourcePoint());
        System.out.println("HP Flea        : " + flea.getHp() + " / " + flea.getMaxHp());
        System.out.println("Status Latihan : " + (canTrain ? "Siap" : "Cooldown (Harus serang/lewatkan waktu dulu)"));
        System.out.println("-----------------------------------------------");
    }

    private void showMenu() {
        System.out.println("1. Serang Flea (Waktu +10s)");
        System.out.println("2. Latihan Pemulihan HP (Waktu +10s)");
        System.out.println("3. Beli Vitamin (Instan, Mengurangi RP)");
        System.out.println("4. Lewati Waktu / Bertahan Diam (Waktu +10s)");
        System.out.print("Pilih aksi (1-4): ");
    }

    // Perbaikan: Validasi ketat agar user tidak memasukkan angka di luar 1-4
    private int inputChoice() {
        int choice;
        while (true) {
            if (!scanner.hasNextInt()) {
                System.out.print("Input harus berupa angka! Pilih aksi (1-4): ");
                scanner.next();
                continue;
            }
            choice = scanner.nextInt();
            if (choice >= 1 && choice <= 4) {
                break;
            }
            System.out.print("Pilihan tidak valid! Pilih aksi (1-4): ");
        }
        return choice;
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                attackFlea();
                canTrain = true; // Mereset cooldown latihan setelah menyerang
                advanceTime(10);
                break;

            case 2:
                if (canTrain) {
                    trainDefender();
                    canTrain = false; // Mengaktifkan cooldown latihan
                    advanceTime(10);
                } else {
                    System.out.println("\n[!] Kamu lelah. Lakukan aksi lain dulu sebelum latihan lagi!");
                }
                break;

            case 3:
                buyVitamin(); // Beli vitamin instan, tidak menambah waktu dunia game
                break;

            case 4:
                System.out.println("\nDefender bersiap siaga di tempat dan membiarkan waktu berjalan.");
                canTrain = true; // Mereset cooldown latihan
                advanceTime(10);
                break;
        }
    }

    private void attackFlea() {
        int damage = defender.attack(flea);
        System.out.println("\n[COMBAT] Defender menyerang Flea sebesar " + damage + " damage.");
        System.out.println("[RISIKO] Defender kelelahan bertarung dan terkena self-damage.");

        if (!flea.isAlive()) {
            int reward = 20;
            defender.addResourcePoint(reward);
            System.out.println("[KILLED] Flea berhasil dikalahkan! +" + reward + " RP didapatkan.");
            flea.respawn();
            System.out.println("[SPAWN] Flea baru yang segar telah muncul.");
        }
    }

    private void trainDefender() {
        int healAmount = defender.train();
        System.out.println("\n[TRAIN] Defender melakukan latihan fisik.");
        if (healAmount > 0) {
            System.out.println("[HEAL] HP Defender pulih sebesar " + healAmount + ".");
        } else {
            System.out.println("[INFO] HP Defender sudah penuh, latihan memperkuat stamina.");
        }
    }

    private void buyVitamin() {
        int result = defender.buyVitamin();
        System.out.println();
        if (result == -2) {
            System.out.println("[SHOP] HP kamu sudah penuh! Vitamin disimpan kembali.");
        } else if (result == -1) {
            System.out.println("[SHOP] RP tidak cukup! Butuh " + defender.getVitaminCost() + " RP.");
        } else {
            System.out.println("[SHOP] Vitamin berhasil dibeli dan diminum!");
            System.out.println("[HEAL] HP Defender pulih sebesar " + result + ".");
        }
    }

    // Perbaikan: Logika jalannya waktu diatur secara linear per detik
    private void advanceTime(int seconds) {
        int targetTime = currentTime + seconds;
        if (targetTime > maxTime) targetTime = maxTime;

        while (currentTime < targetTime && defender.isAlive()) {
            currentTime++;
            
            // Cek apakah detik sekarang adalah jadwal Flea menyerang otomatis
            if (currentTime == nextFleaAttackTime) {
                checkFleaAttack();
                nextFleaAttackTime += fleaAttackInterval;
            }
        }
    }

    private void checkFleaAttack() {
        System.out.println("\n>>> [EVENT] Detik ke-" + currentTime + ": Hama Flea menyerang secara otomatis! <<<");
        int damage = flea.attack(defender);
        System.out.println(">>> Defender terkena serangan otomatis sebesar " + damage + " damage. <<<");

        if (!defender.isAlive()) {
            System.out.println("\n[DEAD] Sisa HP Defender mencapai 0.");
        }
    }

    private void showSummary(boolean win) {
        System.out.println("\n==================================================");
        System.out.println("               RINGKASAN AKHIR GAME");
        System.out.println("==================================================");
        if (win) {
            System.out.println("STATUS AKHIR : BERHASIL BERTAHAN (VICTORY)");
        } else {
            System.out.println("STATUS AKHIR : DEFENDER MATI (GAME OVER)");
        }
        System.out.println("Waktu Akhir  : " + currentTime + " detik");
        System.out.println("Sisa HP      : " + defender.getHp());
        System.out.println("Total RP     : " + defender.getResourcePoint());
        System.out.println("==================================================");
    }
}
