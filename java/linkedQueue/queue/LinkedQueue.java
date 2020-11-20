package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head, tail;
    private static class Node {
        Node next;
        Object element;
        Node(Node next, Object element) {
            this.next = next;
            this.element = element;
        }
    }
    @Override
    public void queueEnq(Object element) {
        Node node = new Node( null, element);
        if (size() == 0) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public Object queueEl() {
        return head.element;
    }

    @Override
    public Object queueDeq() {
        Node res = head;
        head = head.next;
        return res.element;
    }


    @Override
    public void queueCl() {
        head = null;
        tail = null;
    }

    @Override
    public Object[] toArray() {
        Object[] newElements = new Object[size()];
        Node temp = head;
        for (int i = 0; i < size(); i++) {
            newElements[i] = temp.element;
            temp = temp.next;
        }
        return newElements;
    }

}
