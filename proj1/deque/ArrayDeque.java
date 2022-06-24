package deque;

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;
    private static final int MINIMUM_SIZE = 8;

    public ArrayDeque() {
        size = 0;
        nextFirst = 4;
        nextLast = 5;
        items = (T[]) new Object[MINIMUM_SIZE];
    }

    /**
     * convert an pesudo index (which may be negative or larger than items length )
     * to an index which will be in bound
     * @param i
     * @return in bound index
     */
    private int convertIndex(int i) {
        return (i + items.length) % items.length;
    }

    private void resize() {
        int newSize = Math.max(size * 2, MINIMUM_SIZE);
        T[] newItems = (T[]) new Object[newSize];
        int newFirst = size / 2;
        if (nextLast > nextFirst) {
            System.arraycopy(items, convertIndex(nextFirst + 1), newItems, newFirst, size);
        } else {
            System.arraycopy(items, convertIndex(nextFirst + 1), newItems, newFirst,
                    items.length - nextFirst - 1);
            System.arraycopy(items, 0, newItems, newFirst + items.length - 1 - nextFirst,
                    nextLast);
        }
        nextFirst = (newFirst - 1 + newItems.length) % newItems.length;
        nextLast = (nextFirst + size + 1) % newItems.length;
        items = newItems;
    }


    @Override
    public void addFirst(T item) {
        items[nextFirst] = item;
        size += 1;
        nextFirst = convertIndex(nextFirst - 1);
        if (nextFirst == nextLast) {
            resize();
        }
    }

    @Override
    public void addLast(T item) {
        items[nextLast] = item;
        size += 1;
        nextLast = convertIndex(nextLast + 1);
        if (nextLast == nextFirst) {
            resize();
        }
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(items[convertIndex(nextFirst + 1 + i)] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            int firstIndex = convertIndex(nextFirst + 1);
            T first = items[firstIndex];
            items[firstIndex] = null;
            nextFirst = firstIndex;
            size -= 1;
            if (size < items.length / 4 && items.length > MINIMUM_SIZE) {
                resize();
            }
            return first;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            int lastIndex = convertIndex(nextLast - 1);
            T last = items[lastIndex];
            items[lastIndex] = null;
            nextLast = lastIndex;
            size -= 1;
            if (size < items.length / 4 && items.length > MINIMUM_SIZE) {
                resize();
            }
            return last;
        }
    }

    @Override
    public T get(int index) {
        return items[convertIndex(nextFirst + 1 + index)];
    }


    private class ArrayDequeIterator implements Iterator<T> {
        int iterIndex;

        @Override
        public boolean hasNext() {
            return iterIndex < size;
        }

        @Override
        public T next() {
            T item = get(iterIndex);
            iterIndex += 1;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque)) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            Deque<T> other = (Deque<T>) o;
            if (other.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < size(); i += 1) {
                T a = get(i);
                T b = other.get(i);
                if (a instanceof Object
                        && b instanceof Object
                        && !Objects.deepEquals(a, b)) {
                    return false;
                } else if (a instanceof Object && !(b instanceof Object)) {
                    return false;
                } else if (b instanceof Object && !(a instanceof Object)) {
                    return false;
                } else if (!(b instanceof Object) && !(a instanceof Object)) {
                    if (a != b) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
