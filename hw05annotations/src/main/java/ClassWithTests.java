import annotations.*;

public class ClassWithTests {
    @CustomBeforeAll(order = 1)
    public static void beforeAll_first() {
        System.out.println("beforeAll_first");
    }

    @CustomBeforeAll(order = 2)
    public static void beforeAll_second() {
        System.out.println("beforeAll_second");
    }

    @CustomAfterAll(order = 1)
    public static void afterAll_first() {
        System.out.println("afterAll_first");
    }

    @CustomAfterAll(order = 2)
    public static void afterAll_second() {
        System.out.println("afterAll_second");
    }

    @CustomBeforeEach(order = 1)
    public void beforeEach_first() {
        System.out.println("beforeEach_first");
    }

    @CustomBeforeEach(order = 2)
    public void beforeEach_second() {
        System.out.println("beforeEach_second");
    }

    @CustomTest
    public void test_first() throws Exception {
        System.out.println("test_first");
        throw new Exception("exception!!!");
    }

    @CustomTest
    public void test_second() {
        System.out.println("test_second");
    }

    @CustomTest
    public void test_third() {
        System.out.println("test_third");
    }

    @CustomAfterEach(order = 1)
    public void afterEach_first() {
        System.out.println("afterEach_first");
    }

    @CustomAfterEach(order = 2)
    public void afterEach_second() {
        System.out.println("afterEach_second");
    }

    @CustomAfterEach(order = 3)
    public void afterEach_third() {
        System.out.println("afterEach_third");
    }
}
