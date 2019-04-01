import com.google.common.collect.ImmutableList;

public class HelloOtus {
    public static void main(String[] args) {
        ImmutableList.of("A", "B").forEach(System.out::println);
    }
}
