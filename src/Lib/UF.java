package Lib;

/**
 * Groups nodes into connected components using union-find. Use {@code union}
 * to connect two nodes, {@code connected} to check whether they belong to the
 * same group, and {@code count} to get the number of groups. Node indices run
 * from zero to {@code n - 1}. Path compression speeds up repeated searches.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * UF groups = new UF(5);
 * groups.union(0, 1);
 * groups.union(1, 2);
 * System.out.println(groups.connected(0, 2)); // true
 * System.out.println(groups.connected(0, 3)); // false
 * System.out.println(groups.count());        // 3: {0, 1, 2}, {3}, and {4}.
 * }</pre>
 */
class UF {
    // Number of connected components.
    private int count;
    // Store the parent of each node.
    private int[] parent;

    // n is the number of nodes in the graph.
    public UF(int n) {
        this.count = n;
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    // Connect nodes p and q.
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);

        if (rootP == rootQ)
            return;

        parent[rootQ] = rootP;
        // Two connected components have merged into one.
        count--;
    }

    // Check whether nodes p and q are connected.
    public boolean connected(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        return rootP == rootQ;
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Return the number of connected components in the graph.
    public int count() {
        return count;
    }
}
