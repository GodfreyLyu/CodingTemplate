package Lib;
import java.util.*;

/**
 * Stores integers in a stack and keeps track of the smallest value.
 * Pushing, popping, reading the top, and reading the minimum all take constant
 * time. Call {@code pop()}, {@code top()}, and {@code getMin()} only when
 * the stack is not empty.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * MinStack stack = new MinStack();
 * stack.push(3);
 * stack.push(1);
 * stack.push(2);
 * System.out.println(stack.getMin()); // 1
 * System.out.println(stack.top());    // 2
 * stack.pop();                       // Removes 2.
 * stack.pop();                       // Removes 1.
 * System.out.println(stack.getMin()); // 3
 * }</pre>
 */
class MinStack {
    // Store all elements in the stack.
    Stack<Integer> stk = new Stack<>();
    // Track the minimum values as elements are pushed and popped.
    Stack<Integer> minStk = new Stack<>();

    public void push(int val) {
        stk.push(val);
        // Keep the current minimum at the top of minStk.
        if (minStk.isEmpty() || val <= minStk.peek()) {
            // The new element is a minimum of the entire stack.
            minStk.push(val);
        }
    }

    public void pop() {
        // Use equals to compare Integer values in Java.
        if (stk.peek().equals(minStk.peek())) {
            // The element being removed is the current minimum.
            minStk.pop();
        }
        stk.pop();
    }

    public int top() {
        return stk.peek();
    }

    public int getMin() {
        // The top of minStk is the minimum of the entire stack.
        return minStk.peek();
    }
}
