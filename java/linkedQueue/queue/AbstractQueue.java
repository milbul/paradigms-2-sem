package queue;

public abstract class AbstractQueue implements Queue{
    private int size;
    @Override
    public void enqueue(Object element) {
        assert element != null;
        queueEnq(element);
        size++;
    }

    @Override
    public Object element() {
        assert size() > 0;
        return queueEl();
    }

    @Override
    public Object dequeue() {
        assert size() > 0;
        Object res = queueDeq();
        size--;
        return res;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        queueCl();
        size = 0;
    }

    @Override
    public abstract Object[] toArray();

    protected abstract Object queueDeq();
    protected abstract void queueEnq(Object element);
    protected abstract Object queueEl();
    protected abstract void queueCl();
}
