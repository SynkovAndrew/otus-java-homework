import annotations.*;

public class ClassWithTests {
    @CustomBeforeAll
    public static void beforeAll_first() {
        System.out.println("beforeAll_first");
    }

    @CustomBeforeAll
    public static void beforeAll_second() {
        System.out.println("beforeAll_second");
    }

    @CustomBeforeEach
    public void beforeEach_first() {
        System.out.println("beforeEach_first");
    }

    @CustomBeforeEach
    public void beforeEach_second() {
        System.out.println("beforeEach_second");
    }

    @CustomTest
    public void test_first() {
        System.out.println("test_first");
    }

    @CustomTest
    public void test_second() {
        System.out.println("test_second");
    }

    @CustomTest
    public void test_third() {
        System.out.println("test_third");
    }

    @CustomAfterEach
    public void afterEach_first() {
        System.out.println("afterEach_first");
    }

    @CustomAfterEach
    public void afterEach_second() {
        System.out.println("afterEach_second");
    }

    @CustomAfterAll
    public static void afterAll_first() {
        System.out.println("afterAll_first");
    }

    @CustomAfterAll
    public static void afterAll_second() {
        System.out.println("afterAll_second");
    }
}
