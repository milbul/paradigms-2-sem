package search;

public class BinarySearchSpan {

    // Pre : a[i] >= a[i + 1], forall i = 0..a.length - 2 &&
    // (l < r < a.length && a[r] <= x || r = n) && (0 <= l < r && a[l] > x || l = -1)
    // Post : r <= n && a[i'] = a[i] &&
    // 0 < r < n && a[r] <= x && a[r - 1] > x ||  r = n && a[r - 1] > x || r = 0 && a[0] < x
    public static int recursiveBinarySearch (int x, int[] a, int l, int r) {
        if (r <= l + 1) {
            return r;
        }
        int m = (l + r) / 2;
        // l < m < r
        if (a[m] <= x) {
            return recursiveBinarySearch(x, a, l, m);
            // r' = m && a[r'] <= x
            // l' = l && l' < m && (0 <= l < r && a[l] > x || l = -1)
            // a[i'] = a[i]
        }
        return recursiveBinarySearch(x, a, m, r);
        // l' = m && a[l'] > x
        // r' = r && (l' < r' < a.length && a[r'] <= x || r' = n)
        // a[i'] = a[i]
    }

    //Pre : a[i + 1] >= a[i] forall i = 0..a.length - 2
    // Post : r <= n && a[i'] = a[i] &&
    // 0 < r < n && a[r] <= x && a[r - 1] > x ||  r = n && a[r - 1] > x || r = 0 && a[0] < x
    public static int upperBound (int x, int a[]) {
        int l = -1;
        int r = a.length;
        while (r > l + 1) {
            // Inv : l' >= l && r' <= r && r > l + 1 &&
            // a[i + 1] >= a[i], forall i = l..r - 1 &&
            // (l < r < a.length && a[r] <= x || r = n) && (0 <= l < r && a[l] > x || l = -1)
            int m = (l + r) / 2;
            // l < m < r
            if (a[m] >= x) {
                l = m;
                // l' = m && a[l'] > x
                // r' = r && (l' < r' < a.length && a[r'] <= x || r' = n)
                // a[i'] = a[i]
            } else {
                r = m;
                // r' = m && a[r'] <= x
                // l' = l && (0 <= l' < r' && a[l'] > x || l' = -1)
                // a[i'] = a[i]
            }
        }
        return r;
    }

    // Pre : args[i] - integer forall i &&
    // args[i] >= a[i + 1] forall i = 1..args.length - 2 &&
    // args.length > 0
    // Post : args[i] = args[l] forall l <= i < l + span
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }
        int l = recursiveBinarySearch(x, a, -1, a.length);
        int r = upperBound(x, a);
        int span = r - l;
        System.out.println(l + " " + span);
    }
}