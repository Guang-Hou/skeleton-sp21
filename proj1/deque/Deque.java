package deque;

import java.util.Iterator;

public interface Deque<T> {
    /**
     * Adds an item of type T to the front of the deque. You can assume that item is never null.
     */
    public void addFirst(T item);

    /**
     * Adds an item of type T to the back of the deque. You can assume that item is never null.
     */
    public void addLast(T item);

    /**
     * Returns true if deque is empty, false otherwise.
     */
    public default boolean isEmpty() {
        return size() == 0;
    };

    /**
     * Returns the number of items in the deque.
     */
    public int size();

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque();

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    public T removeFirst();

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    public T removeLast();

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * @param index
     * @return item at index
     */
    public T get(int index);

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<T>) so we must provide this method to return an iterator.
     * @return
     */
    public Iterator<T> iterator();

    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and
     * if it contains the same contents (as goverened by the generic T’s equals method)
     * in the same order.
     * @param o
     * @return true or false
     */
    public boolean equals(Object o);

}
