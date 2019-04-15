import java.util.*;

public class DIYArrayList<T> implements List<T> {
    private final int DEFAULT_INIT_SIZE = 10;
    private final int INCREASE_FACTOR = 2;
    private int capacity;
    private int currentIndex;
    private Object[] array;

    public DIYArrayList() {
        capacity = DEFAULT_INIT_SIZE;
        array = new Object[DEFAULT_INIT_SIZE];
        currentIndex = 0;
    }

    public DIYArrayList(final int initialCapacity) {
        capacity = initialCapacity;
        array = new Object[initialCapacity];
        currentIndex = 0;
    }

    public int size() {
        return currentIndex;
    }

    public boolean isEmpty() {
        return currentIndex == 0;
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator() {
        return new DIYIterator();
    }

    public Object[] toArray() {
        return array;
    }

    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    public boolean add(T t) {
        if (currentIndex == capacity) {
            array = getIncreasedArray(array);
            capacity = capacity * INCREASE_FACTOR;
        }
        array[currentIndex] = t;
        currentIndex++;

        return true;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);

        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public T get(int index) {
        checkIndex(index);
        return (T) array[index];
    }

    public T set(int index, T element) {
        checkIndex(index);
        T previous = (T) array[index];
        array[index] = element;
        return previous;
    }

    public void add(int index, T element) {
        checkIndex(index);
        if (index > currentIndex) {
            array[index] = element;
        } else if (index == currentIndex) {
            add(element);
        } else {
            int shift = currentIndex - index;
            if (shift > capacity - currentIndex) {
                array = getIncreasedArray(array);
                capacity = capacity * INCREASE_FACTOR;
            }
            for (int j = currentIndex; j > index; j--) {
                array[j] = array[j - 1];
            }
            array[index] = element;
            currentIndex++;
        }
    }

    public T remove(int index) {
        checkIndex(index);
        T removed = (T) array[index];

        for (int i = index; i < currentIndex; i++) {
            array[i] = array[i + 1];
        }
        currentIndex--;

        return removed;
    }

    public int indexOf(Object o) {
        for (int i = 0; i < currentIndex; i++) {
            if (Objects.equals(o, array[i])) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    public ListIterator<T> listIterator() {
        return new DIYListIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return new DIYListIterator(index);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super T> c) {
        Arrays.sort((T[]) array, 0, currentIndex, c);
    }

    private Object[] getIncreasedArray(Object[] array) {
        final Object[] newArray = new Object[capacity * INCREASE_FACTOR];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        return newArray;
    }

    private void checkIndex(final int index) {
        if (index < 0 || index > capacity) {
            throw new IndexOutOfBoundsException();
        }
    }

    private class DIYIterator implements Iterator<T> {
        private int index;

        private DIYIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < capacity && array[index] != null;
        }

        @Override
        public T next() {
            return index < capacity ? (T) array[index++] : null;
        }
    }

    private class DIYListIterator implements ListIterator<T> {
        public int index;
        private int lastReturned;

        private DIYListIterator() {
            index = 0;
            lastReturned = -1;
        }

        private DIYListIterator(final int i) {
            index = i;
            lastReturned = -1;
        }

        @Override
        public boolean hasNext() {
            return index < capacity && array[index] != null;
        }

        @Override
        public T next() {
            if (index < capacity) {
                lastReturned = index;
                return (T) array[index++];
            }
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0 && array[index - 1] != null;
        }

        @Override
        public T previous() {
            if (index > 0) {
                lastReturned = index - 1;
                return (T) array[--index];
            }
            return null;
        }

        @Override
        public int nextIndex() {
            return index < capacity ? index + 1 : capacity;
        }

        @Override
        public int previousIndex() {
            return index > 0 ? index - 1 : 0;
        }

        @Override
        public void remove() {
            if (lastReturned < 0) {
                throw new IllegalStateException();
            }
            DIYArrayList.this.remove(lastReturned);
            index = lastReturned;
            lastReturned = -1;
        }

        @Override
        public void set(T t) {
            if (lastReturned < 0) {
                throw new IllegalStateException();
            }
            DIYArrayList.this.set(lastReturned, t);
            lastReturned = -1;
        }

        @Override
        public void add(T t) {
            if (lastReturned < 0) {
                throw new IllegalStateException();
            }
            DIYArrayList.this.add(index++, t);
            lastReturned = -1;
        }
    }
}
