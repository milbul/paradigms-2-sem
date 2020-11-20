package queue;

import java.util.Arrays;

public class ArrayQueue  extends AbstractQueue{
    private int head = 0;
    private int tail = 0;
    private Object[] elements = new Object[2];

    public void ensureCapacity () {
        if (size() < elements.length) {
            return;
        }
        elements = Arrays.copyOf(toArray(), 2 * size());
        head = 0;
        tail = size();
    }

    public void queueEnq(Object element) {
        ensureCapacity();
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    public Object queueEl() {
        return elements[head];
    }

    public Object queueDeq() {
        Object element = element();
        head = (head + 1) % elements.length;
        return element;
    }

    public void queueCl() {
        elements = new Object[2];
        head = 0;
        tail = 0;
    }

    public Object[] toArray() {
        Object[] newElements = new Object[size()];
        if (size() != 0) {
            if (head < tail) {
                System.arraycopy(elements, head, newElements, 0, size());
            } else {
                System.arraycopy(elements, head, newElements, 0, elements.length - head);
                System.arraycopy(elements, 0, newElements, elements.length - head, tail);
            }
        }
        return newElements;
    }
}
