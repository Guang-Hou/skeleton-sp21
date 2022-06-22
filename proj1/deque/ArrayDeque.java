package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    int nextFirst;
    int nextLast;
    private T[] items;
    private static final int MINIMUM_SIZE = 8;

    public ArrayDeque() {
        size = 0;
        nextFirst = 4;
        nextLast = 5;
        items = (T[]) new Object[MINIMUM_SIZE];
    }

    private void resize() {
        if (items.length > MINIMUM_SIZE) {
            T[] newItems = (T[]) new Object[size * 2];
            int newFirst = size / 2;
            if (nextLast > nextFirst) {
                System.arraycopy(items, nextFirst + 1, newItems, newFirst, size);
            } else {
                System.arraycopy(items, nextFirst + 1, newItems, newFirst, items.length - nextFirst - 1);
                System.arraycopy(items, 0, newItems, newFirst + items.length - 1 - nextFirst, nextLast);
            }
            nextFirst = newFirst - 1;
            nextLast = nextFirst + size + 1;
            items = newItems;
        }
    }


    @Override
    public void addFirst(T item) {
        items[nextFirst] = item;
        size += 1;
        nextFirst = (items.length + nextFirst - 1) % items.length;
        if (nextFirst == nextLast) {
            resize();
        }
    }

    @Override
    public void addLast(T item) {
        items[nextLast] = item;
        size += 1;
        nextLast = (items.length + nextLast + 1) % items.length;
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
            System.out.print(items[(nextFirst + 1 + i) % items.length] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            int firstIndex = (nextFirst + 1) % items.length;
            T first = items[firstIndex];
            items[firstIndex] = null;
            nextFirst = firstIndex;
            size -= 1;
            if (size < items.length / 4) {
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
            T last = items[nextLast - 1];
            items[nextLast - 1] = null;
            nextLast -= 1;
            size -= 1;
            if (size < items.length / 4) {
                resize();
            }
            return last;
        }
    }

    @Override
    public T get(int index) {
        return items[(nextFirst + 1 + index) % items.length];
    }


    private class ArrayDequeIterator implements Iterator<T> {
        int iter_index;

        @Override
        public boolean hasNext() {
            return iter_index < size;
        }

        @Override
        public T next() {
            T item = get(iter_index);
            iter_index += 1;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            ArrayDeque<T> other = (ArrayDeque<T>) o;
            for (int i = 0; i < size(); i += 1) {
                if (get(i) != other.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }
}
