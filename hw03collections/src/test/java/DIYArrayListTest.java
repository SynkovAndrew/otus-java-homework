import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DIYArrayListTest {
    private static List<Integer> testData;
    private static int count;

    @BeforeAll
    public static void setUp() {
        testData = Stream.iterate(0, i -> i + 10)
                .limit(10000)
                .collect(toList());
        count = testData.size();
    }

    @Test
    public void testAddAll() {
        DIYArrayList<Integer> arrayList = new DIYArrayList<>();

        Collections.addAll(arrayList, testData.toArray(new Integer[0]));

        Assertions.assertEquals(arrayList.size(), count);
        Assertions.assertIterableEquals(testData, arrayList);
    }

    @Test
    public void testCopy() {
        DIYArrayList<Integer> arrayList = new DIYArrayList<>(count);

        Collections.addAll(arrayList, Stream.iterate(0, i -> 0).limit(10000).toArray(Integer[]::new));

        Collections.copy(arrayList, testData);

        Assertions.assertIterableEquals(testData, arrayList);
    }

    @Test
    public void testRemoveByIndex() {
        DIYArrayList<Integer> arrayList = new DIYArrayList<>();
        Collections.addAll(arrayList, Stream.iterate(0, i -> i + 1).limit(11).toArray(Integer[]::new));

        arrayList.remove(1);
        arrayList.remove(3);
        arrayList.remove(4);
        arrayList.remove(8);
        arrayList.remove(9);

        Assertions.assertEquals(arrayList.size(), 6);
        Assertions.assertIterableEquals(Arrays.asList(0, 2, 3, 5, 7, 8, 9, 10), arrayList);
    }

    @Test
    public void testRemove() {
        DIYArrayList<Integer> arrayList = new DIYArrayList<>();
        Collections.addAll(arrayList, Stream.iterate(0, i -> i + 1).limit(11).toArray(Integer[]::new));

        arrayList.remove(Integer.valueOf(1));
        arrayList.remove(Integer.valueOf(3));
        arrayList.remove(Integer.valueOf(6));
        arrayList.remove(Integer.valueOf(7));

        Assertions.assertEquals(arrayList.size(), 7);
        Assertions.assertIterableEquals(Arrays.asList(0, 2, 4, 5, 8, 9, 10), arrayList);
    }

    @Test
    public void testListIterator() {
/*        DIYArrayList<Integer> arrayList = new DIYArrayList<>();
        Collections.addAll(arrayList, Stream.iterate(0, i -> i + 1).limit(13).toArray(Integer[]::new));

        ListIterator<Integer> iterator = arrayList.listIterator();
        iterator.next(); // 0
        iterator.next(); // 1
        iterator.remove(); // 1
        iterator.next(); // 2
        iterator.remove(); // 2
        iterator.next(); // 3
        iterator.next(); // 4
        iterator.remove(); // 4
        iterator.next(); // 5
        iterator.next(); // 6
        iterator.next(); // 7
        iterator.next(); // 8
        iterator.remove(); // 8
        iterator.next(); // 9
        iterator.set(111);
        iterator.next();
        iterator.set(112);
        iterator.add(54);

        Assertions.assertEquals(arrayList.size(), 9);
        Assertions.assertIterableEquals(Arrays.asList(0 ,3, 5, 6, 7, 111, 112, 11, 12), arrayList);*/
        DIYArrayList<String> list = new DIYArrayList<>();
        list.add("hi");
        list.add("whats up");
        list.add("how are you");
        list.add("bye");

        ListIterator<String> iterator = list.listIterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            iterator.add(Integer.toString(i++));
        }

        list.forEach(System.out::println);
    }
}
