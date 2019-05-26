import java.util.ArrayList;

public class GCMain {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 5000000; true; i = i + 50000) {
            final var strings = new ArrayList<String>(i);
            for (int j = 0; j < i; j++) {
                strings.add(String.valueOf(j));
            }
            Thread.sleep(30);
            System.out.println("step: " + i);
        }
    }
}
