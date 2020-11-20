package queue;

public interface Queue {

    // Pre : element != null
    // Post : queue[n' - 1] = element && n' = n + 1
    public void enqueue(Object element);

    // Pre : n > 0
    // Post : R = queue[0] && queue[i'] = queue[i]
    public Object element();

    // Pre : n > 0
    // Post : R = queue[0] && queue[i'] = queue[i] forall i = 1..n - 1 && n' = n - 1
    public Object dequeue();

    // Pre : true
    // Post : R = n && queue[i'] = queue[i] forall i = 0..n - 1
    public int size();

    // Pre : true
    // Post : R = (n == 0) && queue[i'] = queue[i] forall i = 0..n - 1
    public boolean isEmpty();

    // Pre : true
    // Post : n' = 0
    public void clear();

    // Pre : true
    // Post : R[i] = queue[i] forall i = 0..n - 1 && R.length = queue. length
    public Object[] toArray();
}
