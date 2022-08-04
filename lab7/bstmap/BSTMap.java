package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        BSTNode() {
        }

        BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }

    // sentinel node's right child will be the first data node
    private BSTNode sentinel = new BSTNode();
    private int size = 0;

    @Override
    public void clear() {
        sentinel.right = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode node = findNode(key);
        if (node == null) {
            return false;
        }
        return true;
    }

    /**
     * Helper function to find if a node with the target key exists
     * if exists, return the node,
     * otherwise return null
     *
     * @param key
     * @return
     */
    private BSTNode findNode(K key) {
        BSTNode node = sentinel.right;
        while (node != null) {
            if (node.key.compareTo(key) == 0) {
                return node;
            } else if (node.key.compareTo(key) < 0) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return null;
    }


    /**
     * Find the parent node of the target node with target key
     * if exists, return the parent node,
     * otherwise return null
     *
     * @param key
     * @return
     */
    private BSTNode findParent(K key) {
        BSTNode parent = sentinel;
        BSTNode node = sentinel.right;
        while (node != null) {
            if (node.key.compareTo(key) == 0) {
                return parent;
            } else if (node.key.compareTo(key) < 0) {
                parent = node;
                node = node.right;
            } else {
                parent = node;
                node = node.left;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        BSTNode node = findNode(key);
        if (node == null) {
            return null;
        } else {
            return node.value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        sentinel.right = put(key, value, sentinel.right);
        size += 1;
    }

    /**
     * Helper function to insert key-value pair under BSTNode n
     *
     * @param key
     * @param value
     * @param n
     */
    private BSTNode put(K key, V value, BSTNode n) {
        if (n == null) {
            return new BSTNode(key, value);
        }

        int cmp = n.key.compareTo(key);

        if (cmp == 0) {
            n.value = value;
        } else if (cmp < 0) {
            n.right = put(key, value, n.right);
        } else {
            n.left = put(key, value, n.left);
        }

        return n;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        inOrderTraverse(set, sentinel.right);
        return set;
    }

    // node has no left or right child
    private void removeNodeOfNoChild(BSTNode parent, BSTNode node) {
        if (parent.left == node) {
            parent.left = null;
        } else {
            parent.right = null;
        }
    }

    // node only has left child
    private void removeNodeOfSingleLeftChild(BSTNode parent, BSTNode node) {
        if (parent.left == node) {
            parent.left = node.left;
        } else {
            parent.right = node.left;
        }
    }

    // node only has right child
    private void removeNodeOfSingleRightChild(BSTNode parent, BSTNode node) {
        if (parent.left == node) {
            parent.left = node.right;
        } else {
            parent.right = node.right;
        }
    }

    // node has both left and right child
    private void removeNodeOfTwoChild(BSTNode node) {
        BSTNode iop = findInorderPredecessor(node);
        BSTNode iopParent = findParent(iop.key);
        node.key = iop.key;
        node.value = iop.value;
        // delete iop
        if (iop.left == null) {
            iop = null;
        } else {
            removeNodeOfSingleLeftChild(iopParent, iop);
        }
    }

    // Find the in order traversal predecessor of node
    // assume node has both left and right child nodes
    private BSTNode findInorderPredecessor(BSTNode node) {
        BSTNode iop = node.left;
        while (iop.right != null) {
            iop = iop.right;
        }
        return iop;
    }

    @Override
    public V remove(K key) {
        BSTNode parent = findParent(key);
        if (parent == null) {
            return null;
        }

        BSTNode node = findNode(key);
        V res = node.value;

        if (node.left == null && node.right == null) {
            removeNodeOfNoChild(parent, node);
        } else if (node.left != null && node.right == null) {
            removeNodeOfSingleLeftChild(parent, node);
        } else if (node.left == null && node.right != null) {
            removeNodeOfSingleRightChild(parent, node);
        } else {
            removeNodeOfTwoChild(node);
        }

        size -= 1;
        return res;
    }


    /**
     * Delete the node of target key from tree that is closest
     * to the root and return the modified tree. The nodes of
     * the original tree may be modified.
     * https://www-inst.eecs.berkeley.edu//~cs61b/fa14/book2/data-structures.pdf
     */
    private BSTNode remove(BSTNode tree, K key) {
        if (tree == null) {
            return null;
        }
        if (key.compareTo(tree.key) < 0) {
            tree.left = remove(tree.left, key);
        } else if (key.compareTo(tree.key) > 0) {
            tree.right = remove(tree.right, key);
        } else if (tree.left == null) { // Otherwise, we’ve found target key node
            return tree.right;
        } else if (tree.right == null) {
            return tree.left;
        } else {
            tree.left = swapLargest(tree.left, tree);
        }
        return tree;
    }

    /**
     * Move the key from the first node in T (in an inorder
     * traversal) to node R (over-writing the current key of R),
     * remove the first node of T from T, and return the resulting tree.
     */
    private BSTNode swapLargest(BSTNode T, BSTNode R) {
        if (T.right == null) {
            R.key = T.key;
            R.value = T.value;
            return T.left;
        } else {
            T.right = swapLargest(T.right, R);
            return T;
        }
    }


    @Override
    public V remove(K key, V value) {
        BSTNode node = findNode(key);
        if (node == null) {
            return null;
        }

        if (node.value == value || node.value.equals(value)) {
            return remove(key);
        }

        return null;
    }

    @Override
    public Iterator<K> iterator() {
        List<K> data = new LinkedList<>();
        inOrderTraverse(data, sentinel.right);
        return data.iterator();
    }


    public void printInOrder() {
        List<V> data = new LinkedList<>();
        inOrderTraverse(data, sentinel.right);
        System.out.println(data);
    }

    // In order traversal, put the keys in a collection
    // (can be list or set, or other collection type class)
    private void inOrderTraverse(Collection c, BSTNode n) {
        if (n == null) {
            return;
        }

        inOrderTraverse(c, n.left);
        c.add(n.key);
        inOrderTraverse(c, n.right);
    }

}
