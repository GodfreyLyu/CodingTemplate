package Lib;
import java.util.*;

/**
 * Stores integers and removes the most frequent value first. When several
 * values have the same frequency, it removes the most recently pushed one.
 * Call {@code pop()} only when the stack is not empty.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * FreqStack stack = new FreqStack();
 * stack.push(5);
 * stack.push(7);
 * stack.push(5);
 * System.out.println(stack.pop()); // 5: it appears twice.
 * System.out.println(stack.pop()); // 7: it was pushed after the remaining 5.
 * }</pre>
 */
class FreqStack {
    // Track the highest frequency of any value in FreqStack.
    int maxFreq = 0;
    // Map each value to its current frequency.
    HashMap<Integer, Integer> valToFreq = new HashMap<>();
    // Map each frequency to a stack of values that reached that frequency.
    HashMap<Integer, Stack<Integer>> freqToVals = new HashMap<>();

    public void push(int val) {
        // Increase the frequency of val by one.
        int freq = valToFreq.getOrDefault(val, 0) + 1;
        valToFreq.put(val, freq);
        // Push val onto the stack for its new frequency.
        freqToVals.putIfAbsent(freq, new Stack<>());
        freqToVals.get(freq).push(val);
        // Update the maximum frequency.
        maxFreq = Math.max(maxFreq, freq);
    }

    public int pop() {
        // Pop the most recently pushed value with the maximum frequency.
        Stack<Integer> vals = freqToVals.get(maxFreq);
        int v = vals.pop();
        // Decrease the frequency of v by one.
        int freq = valToFreq.get(v) - 1;
        valToFreq.put(v, freq);
        // Update the maximum frequency.
        if (vals.isEmpty()) {
            // No values remain at the current maximum frequency.
            maxFreq--;
        }
        return v;
    }
}
