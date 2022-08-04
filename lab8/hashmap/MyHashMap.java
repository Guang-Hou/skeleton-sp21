package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author Guang Hou
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /* Instance Variables */
    private Collection<Node>[] buckets;
    /* The size (number of buckets) of the hash map. */
    private int tableSize = 16;
    /* The default max load factor. */
    private double maxLoad = 0.75;
    /* Track the number of elements in the hash map. */
    private int numOfElements = 0;

    /**
     * Default Constructor
     */
    public MyHashMap() {
        buckets = createTable(tableSize);
    }

    /**
     * Constructor with parameter initial size.
     *
     * @param initialSize The initial size of the hash table.
     */
    public MyHashMap(int initialSize) {
        tableSize = initialSize;
        buckets = createTable(tableSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= maxLoad
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        tableSize = initialSize;
        this.maxLoad = maxLoad;
        buckets = createTable(tableSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param size the size of the table to create
     */
    private Collection<Node>[] createTable(int size) {
        return new Collection[size];
    }

    /**
     * Clear the hash table.
     */
    @Override
    public void clear() {
        tableSize = 16;
        maxLoad = 0.75;
        numOfElements = 0;
        buckets = createTable(tableSize);
    }

    /**
     * Check if the hash table contains the key.
     *
     * @param key The key to be checked.
     * @return True if the key exists, false if not.
     */
    @Override
    public boolean containsKey(K key) {
        if (get(key) != null) {
            return true;
        }
        return false;
    }

    /**
     * Get the related value which is related to the provided key.
     *
     * @param key User provided key.
     * @return The related value if the key exists in the hash table,
     * null if the key doesn't exist.
     */
    @Override
    public V get(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(), tableSize);
        Collection<Node> targetBucket = buckets[bucketIndex];

        if (targetBucket == null) {
            return null;
        }

        for (Node n : targetBucket) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }

        return null;
    }

    /**
     * Return the number of elements in the hash table.
     *
     * @return The number of elements.
     */
    @Override
    public int size() {
        return numOfElements;
    }

    /**
     * Put the key-value pair into the hash table.
     * If the key doens't exist before, this will be a new entry,
     * increae the numOfElements by 1.
     *
     * @param key   The key.
     * @param value The value.
     */
    @Override
    public void put(K key, V value) {
        boolean newEntry = put(key, value, buckets);
        if (newEntry) {
            numOfElements += 1;
            reSize();
        }
    }

    /**
     * Check if load factor is larger than maxLoad,
     * if so, resize the hash map.
     */
    private void reSize() {
        if (numOfElements / tableSize > maxLoad) {
            tableSize *= 2;
            Collection[] newBuckets = new Collection[tableSize];

            for (Collection<Node> bucket : buckets) {
                if (bucket != null) {
                    for (Node n : bucket) {
                        put(n.key, n.value, newBuckets);
                    }
                }
            }

            buckets = newBuckets;
        }
    }

    /**
     * Helper function to put (key, value) pair into a Collection<Node>[] buckets.
     *
     * @param key     The key.
     * @param value   The value.
     * @param hashBuckets The Collection<Node> buckets where the (key, value) to be stored.
     * @return true if the key doesn't exist and this paie is a new element;
     * false if the key already exists and we update its value.
     */
    private boolean put(K key, V value, Collection<Node>[] hashBuckets) {
        Node newEle = createNode(key, value);
        int bucketIndex = Math.floorMod(key.hashCode(), hashBuckets.length);
        Collection<Node> targetBucket = hashBuckets[bucketIndex];

        boolean newEntry = true;

        if (targetBucket == null) {
            hashBuckets[bucketIndex] = createBucket();
            hashBuckets[bucketIndex].add(newEle);
            return newEntry;
        }

        Iterator<Node> targetBucketIterator = targetBucket.iterator();

        while (targetBucketIterator.hasNext()) {
            Node n = targetBucketIterator.next();
            if (n.key.equals(key)) {
                targetBucketIterator.remove();
                newEntry = false;
            }
        }
        targetBucket.add(newEle);
        return newEntry;
    }

    /**
     * Generate a set of all keys in the table.
     *
     * @return A set of keys.
     */
    @Override
    public Set<K> keySet() {
        Set<K> allElements = new HashSet<>();
        for (int i = 0; i < tableSize; i += 1) {
            Collection<Node> bucket = buckets[i];
            if (bucket != null) {
                for (Node n : bucket) {
                    allElements.add(n.key);
                }
            }
        }

        return allElements;
    }

    /**
     * Remove the key-value pair if the given key exists in the table.
     *
     * @param key The key to be removed.
     * @return The related value if the key exists. If not, return null.
     */
    @Override
    public V remove(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(), tableSize);
        Collection<Node> targetBucket = buckets[bucketIndex];

        V value = null;
        Iterator<Node> targetBucketIterator = targetBucket.iterator();

        while (targetBucketIterator.hasNext()) {
            Node n = targetBucketIterator.next();
            if (n.key.equals(key)) {
                value = n.value;
                targetBucketIterator.remove();
            }
        }

        return value;
    }

    /**
     * Remove the key-value pair if the exact pair exists.
     *
     * @param key   The provided key.
     * @param value The provided value.
     * @return The removed value if key-value pair exists, otherwise return null.
     */
    @Override
    public V remove(K key, V value) {
        if (value.equals(get(key))) {
            return remove(key);
        }
        return null;
    }

    /**
     * Iterator to iterate the hash table keys.
     *
     * @return Hash table keys iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }
}
