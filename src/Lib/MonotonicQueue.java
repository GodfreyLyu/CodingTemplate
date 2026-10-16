package Lib;

import java.util.LinkedList;

/**
 * Stores elements in first-in, first-out order while tracking the current
 * minimum and maximum. Useful for finding extremes in a sliding window.
 * Elements must be non-null and comparable. Call {@code pop()}, {@code min()},
 * and {@code max()} only when the queue is not empty.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * MonotonicQueue<Integer> queue = new MonotonicQueue<>();
 * queue.push(3);
 * queue.push(1);
 * queue.push(2);
 * System.out.println(queue.max());  // 3
 * System.out.println(queue.min());  // 1
 * System.out.println(queue.pop());  // 3: the first element added.
 * System.out.println(queue.max());  // 2
 * System.out.println(queue.size()); // 2
 * }</pre>
 *
 * @param <E> the type of elements stored in the queue
 */
class MonotonicQueue<E extends Comparable<E>> {
    // A regular queue that stores all elements.
    LinkedList<E> q = new LinkedList<>();
    // A non-increasing queue with the maximum at the front.
    LinkedList<E> maxq = new LinkedList<>();
    // A non-decreasing queue with the minimum at the front.
    LinkedList<E> minq = new LinkedList<>();

    public void push(E elem) {
        // Append the element to the regular queue.
        q.addLast(elem);

        // Remove elements smaller than elem from the back of maxq.
        while (!maxq.isEmpty() && maxq.getLast().compareTo(elem) < 0) {
            maxq.pollLast();
        }
        maxq.addLast(elem);

        // Remove elements larger than elem from the back of minq.
        while (!minq.isEmpty() && minq.getLast().compareTo(elem) > 0) {
            minq.pollLast();
        }
        minq.addLast(elem);
    }

    public E max() {
        // The front of maxq holds the maximum element.
        return maxq.getFirst();
    }

    public E min() {
        // The front of minq holds the minimum element.
        return minq.getFirst();
    }

    public E pop() {
        // Remove the element at the front of the regular queue.
        E deleteVal = q.pollFirst();
        assert deleteVal != null;

        // The element may already have been removed from maxq or minq during push.
        if (deleteVal.equals(maxq.getFirst())) {
            maxq.pollFirst();
        }
        if (deleteVal.equals(minq.getFirst())) {
            minq.pollFirst();
        }
        return deleteVal;
    }

    public int size() {
        // The regular queue stores all elements, so its size is the current queue size.
        return q.size();
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }
}
