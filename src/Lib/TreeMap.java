package Lib;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Stores key-value pairs in an unbalanced binary search tree. Keys stay in
 * sorted order, allowing range queries, nearest-key lookups, and access by
 * sorted position. {@code rank} counts smaller keys; {@code select} uses a
 * zero-based index. Keys must be non-null and comparable.
 *
 * <p>Example using this project's {@code Lib.TreeMap}:
 * <pre>{@code
 * Lib.TreeMap<Integer, String> map = new Lib.TreeMap<>();
 * map.put(20, "twenty");
 * map.put(10, "ten");
 * map.put(30, "thirty");
 * System.out.println(map.get(20));        // twenty
 * System.out.println(map.keys());         // [10, 20, 30]
 * System.out.println(map.keys(10, 20));   // [10, 20]
 * System.out.println(map.floorKey(25));   // 20
 * System.out.println(map.ceilingKey(25)); // 30
 * System.out.println(map.rank(20));       // 1
 * System.out.println(map.select(0));      // 10
 * map.remove(20);
 * System.out.println(map.size());         // 2
 * }</pre>
 *
 * @param <K> the type of keys, ordered by their natural comparison
 * @param <V> the type of values associated with the keys
 */
public class TreeMap<K extends Comparable<K>, V> {

    /**
     * Holds one key-value pair, links to its children, and the number of nodes
     * in its subtree. TreeMap uses the subtree size for rank and index queries.
     * This private helper is created and managed by the enclosing map.
     *
     * <p>Example of creating nodes through the map's public methods:
     * <pre>{@code
     * Lib.TreeMap<Integer, String> map = new Lib.TreeMap<>();
     * map.put(2, "two"); // Creates the root node with subtree size 1.
     * map.put(1, "one"); // Adds a left child; the root's subtree size becomes 2.
     * System.out.println(map.rank(2)); // 1
     * }</pre>
     */
    private class TreeNode {
        K key;
        V val;
        TreeNode left, right;
        // Number of nodes in the binary search tree (BST) rooted at this node.
        int size;

        TreeNode(K key, V val) {
            this.key = key;
            this.val = val;
            this.size = 1;
            left = right = null;
        }
    }

    private TreeNode root = null;

    public TreeMap() {
    }

    // **** Insertion and updates ****

    // Add the key-value pair, or update the value if key already exists.
    public V put(K key, V val) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        V oldVal = get(key);

        root = put(root, key, val);

        return oldVal;
    }

    private TreeNode put(TreeNode node, K key, V val) {
        if (node == null) {
            // The key does not exist, so create a new node.
            return new TreeNode(key, val);
        }
        int cmp = node.key.compareTo(key);

        if (cmp > 0) {
            // node.key > key: search the left subtree.
            node.left = put(node.left, key, val);
        } else if (cmp < 0) {
            // node.key < key
            node.right = put(node.right, key, val);
        } else {
            // node.key == key: the key was found.
            node.val = val;
        }

        // Update the size of this subtree.
        node.size = size(node.left) + size(node.right) + 1;

        return node;
    }

    // **** Removal ****

    // Remove key and return its value.
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (!containsKey(key)) {
            return null;
        }

        V deletedVal = get(key);
        root = remove(root, key);

        return deletedVal;
    }

    private TreeNode remove(TreeNode node, K key) {
        int cmp = node.key.compareTo(key);
        if (cmp > 0) {
            // node.key > key: search the left subtree.
            node.left = remove(node.left, key);
        } else if (cmp < 0) {
            // node.key < key: search the right subtree.
            node.right = remove(node.right, key);
        } else {
            // node.key == key: remove the matching node.

            // 1. The node is a leaf: both children are null.
            // 2. The node has exactly one non-null child.
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            // 3. The node has two non-null children.
            // 3.1 Find leftMax, the node with the largest key in the left subtree.
            TreeNode leftMax = maxNode(node.left);
            // 3.2 Remove that node from the left subtree.
            node.left = removeMax(node.left);
            // 3.3 Replace node with leftMax.
            leftMax.left = node.left;
            leftMax.right = node.right;
            node = leftMax;
        }

        // Update the size of this subtree.
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    // Remove the smallest key from the BST.
    public void removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        root = removeMin(root);
    }

    // Remove the smallest node from this subtree and return the updated subtree root.
    private TreeNode removeMin(TreeNode node) {
        if (node.left == null) {
            // The leftmost node has been reached.
            return node.right;
        }
        node.left = removeMin(node.left);

        // Update the size of this subtree.
        node.size = size(node.left) + size(node.right) + 1;

        return node;
    }

    // Remove the largest key from the BST.
    public void removeMax() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        root = removeMax(root);
    }

    // Remove the largest node from this subtree and return the updated subtree root.
    private TreeNode removeMax(TreeNode node) {
        if (node.right == null) {
            // The rightmost node has been reached.
            return node.left;
        }
        node.right = removeMax(node.right);

        // Update the size of this subtree.
        node.size = size(node.left) + size(node.right) + 1;

        return node;
    }

    // **** Queries ****

    // Return the value for key, or null if key does not exist.
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        TreeNode x = get(root, key);
        if (x == null) {
            // The key does not exist.
            return null;
        }

        return x.val;
    }

    // Search for key in the BST rooted at node.
    private TreeNode get(TreeNode node, K key) {
        if (node == null) {
            // The key does not exist.
            return null;
        }

        int cmp = node.key.compareTo(key);

        // node.key > key
        if (cmp > 0) {
            return get(node.left, key);
        }

        // node.key < key
        if (cmp < 0) {
            return get(node.right, key);
        }

        // node.key == key
        return node;
    }

    // Return the largest key less than or equal to key.
    public K floorKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        TreeNode x = floorKey(root, key);
        return x.key;
    }

    private TreeNode floorKey(TreeNode node, K key) {
        if (node == null) {
            // No candidate was found in this subtree.
            return null;
        }

        int cmp = node.key.compareTo(key);

        // node.key > key: search the left subtree.
        if (cmp > 0) {
            return floorKey(node.left, key);
        }

        // node.key < key: search the right subtree.
        if (cmp < 0) {
            TreeNode x = floorKey(node.right, key);
            if (x == null) {
                return node;
            }
            return x;
        }

        // node.key == key
        return node;
    }

    // Return the smallest key greater than or equal to key.
    public K ceilingKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        TreeNode x = ceilingKey(root, key);
        return x.key;
    }

    private TreeNode ceilingKey(TreeNode node, K key) {
        if (node == null) {
            // No candidate was found in this subtree.
            return null;
        }

        int cmp = node.key.compareTo(key);

        // node.key > key: search the left subtree.
        if (cmp > 0) {
            TreeNode x = ceilingKey(node.left, key);
            if (x == null) {
                return node;
            }
            return x;
        }

        // node.key < key: search the right subtree.
        if (cmp < 0) {
            return ceilingKey(node.right, key);
        }

        // node.key == key
        return node;
    }

    // Return the number of keys smaller than key.
    public int rank(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        return rank(root, key);
    }

    // Count the keys smaller than key in the BST rooted at node.
    private int rank(TreeNode node, K key) {
        if (node == null) {
            return 0;
        }
        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            // key < node.key
            // Skip this node and its right subtree.
            // Their keys are all greater than key.
            return rank(node.left, key);
        } else if (cmp > 0) {
            // key > node.key
            // This node and every key in its left subtree are smaller than key.
            return size(node.left) + 1 + rank(node.right, key);
        } else {
            // key == node.key
            return size(node.left);
        }
    }

    // Return the key at index i in sorted order, using zero-based indexing.
    public K select(int i) {
        if (i < 0 || i >= size()) {
            throw new IllegalArgumentException();
        }

        TreeNode x = select(root, i);
        return x.key;
    }

    // Return the node at index i in the sorted order of this subtree.
    private TreeNode select(TreeNode node, int i) {
        int n = size(node.left);

        if (n > i) {
            // n == 10, i == 3
            return select(node.left, i);
        } else if (n < i) {
            // n == 3, i == 10
            return select(node.right, i - n - 1);
        } else {
            // i == n
            // This node is at index i.
            return node;
        }
    }

    // Return the largest key in the BST.
    public K maxKey() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        TreeNode p = maxNode(root);
        return p.key;
    }

    private TreeNode maxNode(TreeNode p) {
        while (p.right != null) {
            p = p.right;
        }
        return p;
    }

    // Return the smallest key in the BST.
    public K minKey() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        TreeNode p = minNode(root);
        return p.key;
    }

    private TreeNode minNode(TreeNode p) {
        while (p.left != null) {
            p = p.left;
        }
        return p;
    }

    // Check whether the map contains key.
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        return get(root, key) != null;
    }

    // **** Helper methods ****

    // Return all keys in ascending order.
    public Iterable<K> keys() {
        if (isEmpty()) {
            return new LinkedList<>();
        }
        LinkedList<K> list = new LinkedList<>();
        traverse(root, list);
        return list;
    }

    // Traverse the BST in order.
    private void traverse(TreeNode node, LinkedList<K> list) {
        if (node == null) {
            return;
        }
        traverse(node.left, list);
        // Visit this node between its left and right subtrees.
        list.addLast(node.key);
        traverse(node.right, list);
    }

    // Return keys in the inclusive range [min, max] in ascending order.
    public Iterable<K> keys(K min, K max) {
        if (min == null) throw new IllegalArgumentException("min is null");
        if (max == null) throw new IllegalArgumentException("max is null");

        LinkedList<K> list = new LinkedList<>();
        traverse(root, list, min, max);
        return list;
    }

    // Traverse the BST in order.
    private void traverse(TreeNode node, LinkedList<K> list, K min, K max) {
        if (node == null) {
            return;
        }

        int cmpMin = min.compareTo(node.key);
        int cmpMax = max.compareTo(node.key);

        if (cmpMin < 0) {
            // min < node.key
            traverse(node.left, list, min, max);
        }

        // Include this key if min <= node.key <= max.
        if (cmpMin <= 0 && cmpMax >= 0) {
            list.addLast(node.key);
        }

        if (cmpMax > 0) {
            // max > node.key
            traverse(node.right, list, min, max);
        }
    }

    public int size() {
        return size(root);
    }

    // Return the number of nodes in the BST rooted at node.
    private int size(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

}
