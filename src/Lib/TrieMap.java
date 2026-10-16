package Lib;
import java.util.*;

/**
 * Maps strings to values using a trie, which shares paths between keys with
 * the same prefix. Supports exact lookups, prefix searches, and patterns where
 * {@code .} matches any single character. Use non-null keys and values, and
 * characters with numeric values from 0 through 255.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * TrieMap<Integer> words = new TrieMap<>();
 * words.put("app", 1);
 * words.put("apple", 2);
 * words.put("bat", 3);
 * System.out.println(words.get("apple"));                 // 2
 * System.out.println(words.keysWithPrefix("app"));        // [app, apple]
 * System.out.println(words.shortestPrefixOf("applesauce")); // app
 * System.out.println(words.longestPrefixOf("applesauce"));  // apple
 * System.out.println(words.keysWithPattern("b.t"));       // [bat]
 * words.remove("app");
 * System.out.println(words.containsKey("app"));           // false
 * }</pre>
 *
 * @param <V> the type of values associated with the string keys
 */
class TrieMap<V> {
    // Number of supported character values (0 through 255).
    private static final int R = 256;
    // Number of key-value pairs currently in the map.
    private int size = 0;
    // Root node of the trie.
    private TrieNode<V> root = null;

    /**
     * Represents a prefix in the trie. Each child slot corresponds to the next
     * character in a key. A non-null value marks the end of a stored key.
     * This private helper is created and managed by TrieMap.
     *
     * <p>Example inside the {@code Lib} package, using the map to create nodes:
     * <pre>{@code
     * TrieMap<Integer> words = new TrieMap<>();
     * words.put("at", 1); // Creates the path from the root through 'a' to 't'.
     * words.put("as", 2); // Reuses the 'a' node and adds an 's' child.
     * System.out.println(words.keysWithPrefix("a")); // [as, at]
     * }</pre>
     *
     * @param <V> the type of value stored at the end of a key
     */
    private static class TrieNode<V> {
        V val = null;
        TrieNode<V>[] children = new TrieNode[R];
    }

    // **** Insertion and updates ****

    // Add or update a key-value pair in the map.
    public void put(String key, V val) {
        if (!containsKey(key)) {
            // Count the new key-value pair.
            size++;
        }
        // Use the helper's return value to update the root.
        root = put(root, key, val, 0);
    }

    // Insert key[i..] into the trie rooted at node and return the updated root.
    private TrieNode<V> put(TrieNode<V> node, String key, V val, int i) {
        if (node == null) {
            // Create a node if this branch does not exist.
            node = new TrieNode<>();
        }
        if (i == key.length()) {
            // The full key path is present; store val at this node.
            node.val = val;
            return node;
        }
        char c = key.charAt(i);
        // Insert recursively and update the child reference.
        node.children[c] = put(node.children[c], key, val, i + 1);
        return node;
    }

    // **** Removal ****

    // Remove key from the map.
    public void remove(String key) {
        if (!containsKey(key)) {
            return;
        }
        // Update the root with the result of the recursive removal.
        root = remove(root, key, 0);
        size--;
    }

    // Remove key[i..] from the trie rooted at node and return the updated root.
    private TrieNode<V> remove(TrieNode<V> node, String key, int i) {
        if (node == null) {
            return null;
        }
        if (i == key.length()) {
            // The node for key was found; clear its value.
            node.val = null;
        } else {
            char c = key.charAt(i);
            // Recursively remove the key from the child subtree.
            node.children[c] = remove(node.children[c], key, i + 1);
        }
        // After processing the children, prune unused nodes along the path.
        if (node.val != null) {
            // Keep this node because it still stores a value.
            return node;
        }
        // Check whether this node still has any children.
        for (int c = 0; c < R; c++) {
            if (node.children[c] != null) {
                // Keep this node if it has at least one child.
                return node;
            }
        }
        // Remove this node because it has neither a value nor children.
        return null;
    }

    // **** Queries ****

    // Return the value for key, or null if it does not exist.
    public V get(String key) {
        // Search for key starting at the root.
        TrieNode<V> x = getNode(root, key);
        if (x == null || x.val == null) {
            // A missing node or a null value means key has no associated value.
            return null;
        }
        return x.val;
    }

    // Check whether the map contains key.
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    // Check whether any key starts with prefix.
    public boolean hasKeyWithPrefix(String prefix) {
        // The prefix exists if its path leads to a node.
        return getNode(root, prefix) != null;
    }

    // Find the shortest stored key that is a prefix of query.
    public String shortestPrefixOf(String query) {
        TrieNode<V> p = root;
        // Follow the characters of query from the root.
        for (int i = 0; i < query.length(); i++) {
            if (p == null) {
                // The path ends here, so the search cannot continue.
                return "";
            }
            if (p.val != null) {
                // A stored key is a prefix of query.
                return query.substring(0, i);
            }
            // Follow the next character to its child node.
            char c = query.charAt(i);
            p = p.children[c];
        }

        if (p != null && p.val != null) {
            // The entire query is a stored key.
            return query;
        }
        return "";
    }

    // Find the longest stored key that is a prefix of query.
    public String longestPrefixOf(String query) {
        TrieNode<V> p = root;
        // Track the length of the longest matching prefix.
        int max_len = 0;

        // Follow the characters of query from the root.
        for (int i = 0; i < query.length(); i++) {
            if (p == null) {
                // The path ends here, so the search cannot continue.
                break;
            }
            if (p.val != null) {
                // A stored key is a prefix of query; update the longest matching length.
                max_len = i;
            }
            // Follow the next character to its child node.
            char c = query.charAt(i);
            p = p.children[c];
        }

        if (p != null && p.val != null) {
            // The entire query is a stored key.
            return query;
        }
        return query.substring(0, max_len);
    }

    // Find all keys that start with prefix.
    public List<String> keysWithPrefix(String prefix) {
        List<String> res = new LinkedList<>();
        // Find the trie node at the end of prefix.
        TrieNode<V> x = getNode(root, prefix);
        if (x == null) {
            return res;
        }
        // Use depth-first search to explore the trie rooted at x.
        traverse(x, new StringBuilder(prefix), res);
        return res;
    }

    // Traverse the trie rooted at node to collect all keys.
    private void traverse(TrieNode<V> node, StringBuilder path, List<String> res) {
        if (node == null) {
            // This branch has no node, so stop exploring it.
            return;
        }

        if (node.val != null) {
            // A key was found; add it to the results.
            res.add(path.toString());
        }

        // Explore each branch using backtracking.
        for (char c = 0; c < R; c++) {
            // Append the next character to the path.
            path.append(c);
            traverse(node.children[c], path, res);
            // Remove the character when backtracking.
            path.deleteCharAt(path.length() - 1);
        }
    }

    // The wildcard '.' matches any single character.
    public List<String> keysWithPattern(String pattern) {
        List<String> res = new LinkedList<>();
        traverse(root, new StringBuilder(), pattern, 0, res);
        return res;
    }

    // Search the trie rooted at node for keys matching pattern[i..].
    private void traverse(TrieNode<V> node, StringBuilder path, String pattern, int i, List<String> res) {
        if (node == null) {
            // This branch does not exist, so it cannot match.
            return;
        }
        if (i == pattern.length()) {
            // All characters in pattern have been matched.
            if (node.val != null) {
                // A value at this node means a matching key was found.
                res.add(path.toString());
            }
            return;
        }
        char c = pattern.charAt(i);
        if (c == '.') {
            // pattern[i] is a wildcard that matches any single character.
            // Explore all child branches using backtracking.
            for (char j = 0; j < R; j++) {
                path.append(j);
                traverse(node.children[j], path, pattern, i + 1, res);
                path.deleteCharAt(path.length() - 1);
            }
        } else {
            // pattern[i] is the literal character c.
            path.append(c);
            traverse(node.children[c], path, pattern, i + 1, res);
            path.deleteCharAt(path.length() - 1);
        }
    }

    // Check whether any key matches pattern; '.' matches any single character.
    public boolean hasKeyWithPattern(String pattern) {
        // Start matching pattern[0..] at the root.
        return hasKeyWithPattern(root, pattern, 0);
    }

    // Return whether pattern[i..] matches a key starting from node.
    private boolean hasKeyWithPattern(TrieNode<V> node, String pattern, int i) {
        if (node == null) {
            // This branch does not exist, so it cannot match.
            return false;
        }
        if (i == pattern.length()) {
            // The pattern is complete; check whether this node represents a stored key.
            return node.val != null;
        }
        char c = pattern.charAt(i);
        // Handle a literal character.
        if (c != '.') {
            // Continue matching pattern[i+1..] from node.children[c].
            return hasKeyWithPattern(node.children[c], pattern, i + 1);
        }
        // Handle a wildcard by trying each child.
        for (int j = 0; j < R; j++) {
            // The wildcard can match any character; return as soon as a match is found.
            if (hasKeyWithPattern(node.children[j], pattern, i + 1)) {
                return true;
            }
        }
        // None of the branches matched.
        return false;
    }

    // Find the node for key starting from node, or return null if the path is missing.
    private TrieNode<V> getNode(TrieNode<V> node, String key) {
        TrieNode<V> p = node;
        // Follow the characters of key starting from node.
        for (int i = 0; i < key.length(); i++) {
            if (p == null) {
                // The path ends here, so the search cannot continue.
                return null;
            }
            // Follow the next character to its child node.
            char c = key.charAt(i);
            p = p.children[c];
        }
        return p;
    }

    public int size() {
        return size;
    }
}
