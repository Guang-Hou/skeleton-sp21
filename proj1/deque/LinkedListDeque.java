package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private final Node sentinel;
    private int size;

    private class Node {
        T item;
        Node prev;
        Node next;

        Node() {
        }

        Node(T item) {
            this.item = item;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        size += 1;
        Node cur = new Node(item);
        cur.next = sentinel.next;
        sentinel.next.prev = cur;
        sentinel.next = cur;
        cur.prev = sentinel;
    }

    @Override
    public void addLast(T item) {
        size += 1;
        Node cur = new Node(item);
        sentinel.prev.next = cur;
        cur.prev = sentinel.prev;
        sentinel.prev = cur;
        cur.next = sentinel;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print("" + p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (sentinel.next != sentinel) {
            Node first = sentinel.next;
            sentinel.next = first.next;
            first.next.prev = sentinel;
            size -= 1;
            return first.item;
        } else {
            return null;
        }
    }

    @Override
    public T removeLast() {
        if (sentinel.prev != sentinel) {
            Node last = sentinel.prev;
            sentinel.prev = last.prev;
            last.prev.next = sentinel;
            size -= 1;
            return last.item;
        } else {
            return null;
        }
    }

    @Override
    public T get(int index) {
        if (index < size) {
            Node p = sentinel.next;
            for (int i = 0; i < index; i += 1) {
                p = p.next;
            }
            return p.item;
        } else {
            return null;
        }
    }

    public T getRecursive(int index) {
        if (index < size) {
            return getRecursiveHelper(sentinel, index + 1);
        } else {
            return null;
        }
    }

    /**
     * return the index-item after node cur
     *
     * @param cur
     * @param index
     * @return
     */
    private T getRecursiveHelper(Node cur, int index) {
        if (index == 0) {
            return cur.item;
        }
        return getRecursiveHelper(cur.next, index - 1);
    }

    private class LinkedListDequeIterator implements Iterator<T> {

        Node p = sentinel.next;

        @Override
        public boolean hasNext() {
            return !(p.next == sentinel);
        }

        @Override
        public T next() {
            T item = p.item;
            p = p.next;
            return item;
        }

    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
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
                } else if ( a instanceof Object && !(b instanceof Object)) {
                    return false;
                } else if (b instanceof Object && !(a instanceof Object)) {
                    return false;
                } else if (a != b) {
                    return false;
                }
            }
        }
        return true;
    }

//    @Override
//    public int hashCode() {
//        return this.toString().hashCode();
//    }
}
