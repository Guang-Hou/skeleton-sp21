package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> implements Comparable<T>{
    private Comparator<T> myComparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.myComparator = c;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        } else {
            T maxItem = this.get(0);
            for (int i = 0; i < this.size(); i += 1) {
                if (myComparator.compare(maxItem, get(i)) < 0) {
                    maxItem = this.get(i);
                }
            }
            return maxItem;
        }
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        } else {
            T maxItem = this.get(0);
            for (int i = 0; i < size(); i += 1) {
                if (c.compare(maxItem, get(i)) < 0) {
                    maxItem = get(i);
                }
            }
            return maxItem;
        }
    }


    @Override
    public int compareTo(Object o) {
        return this.size() - ((MaxArrayDeque<T>) o).size();
    }
}

