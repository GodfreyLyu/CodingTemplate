package Lib;

import java.util.*;

/**
 * Evaluates integer arithmetic expressions with parentheses and the operators
 * {@code +}, {@code -}, {@code *}, and {@code /}. Multiplication and division
 * take priority over addition and subtraction. Division rounds toward zero.
 *
 * <p>Example:
 * <pre>{@code
 * Caculator calculator = new Caculator();
 * System.out.println(calculator.calculate("2 * (3 + 4)")); // 14
 * System.out.println(calculator.calculate("7 / 2"));       // 3
 * }</pre>
 */
public class Caculator {
    public int calculate(String s) {
        // Map each opening parenthesis index to its matching closing parenthesis index.
        Map<Integer, Integer> rightIndex = new HashMap<>();
        // Use a stack to find matching parentheses.
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else if (s.charAt(i) == ')') {
                rightIndex.put(stack.pop(), i);
            }
        }
        return _calculate(s, 0, s.length() - 1, rightIndex);
    }

    // Evaluate the expression in s[start..end], including both endpoints.
    private int _calculate(String s, int start, int end, Map<Integer, Integer> rightIndex) {
        // Use a stack to store intermediate results.
        Stack<Integer> stk = new Stack<>();
        // Track the current number in the expression.
        int num = 0;
        // Track the operator before num, starting with '+'.
        char sign = '+';
        for (int i = start; i <= end; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num = 10 * num + (c - '0');
            }
            if (c == '(') {
                // Recursively evaluate the expression inside the parentheses.
                num = _calculate(s, i + 1, rightIndex.get(i) - 1, rightIndex);
                i = rightIndex.get(i);
            }
            if (c == '+' || c == '-' || c == '*' || c == '/' || i == end) {
                int pre;
                switch (sign) {
                    case '+':
                        stk.push(num);
                        break;
                    case '-':
                        stk.push(-num);
                        break;
                    // Apply multiplication or division to the previous value on the stack.
                    case '*':
                        pre = stk.pop();
                        stk.push(pre * num);
                        break;
                    case '/':
                        pre = stk.pop();
                        stk.push(pre / num);
                        break;
                }
                // Save the current operator and reset the number to zero.
                sign = c;
                num = 0;
            }
        }
        // Sum all values in the stack to get the result.
        int res = 0;
        while (!stk.isEmpty()) {
            res += stk.pop();
        }
        return res;
    }
}
