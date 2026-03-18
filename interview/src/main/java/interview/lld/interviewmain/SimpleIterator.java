package interview.lld.interviewmain;

// Custom iterator interface
interface MyIterator {
    boolean hasNext();
    int next();
}

// Custom collection
class MyCollection {

    private int[] data;

    public MyCollection(int[] data) {
        this.data = data;
    }

    public MyIterator iterator() {
        return new MyCollectionIterator();
    }

    // Inner iterator implementation
    private class MyCollectionIterator implements MyIterator {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < data.length;
        }

        @Override
        public int next() {
            if (!hasNext()) {
                throw new RuntimeException("No more elements");
            }
            return data[index++];
        }
    }
}

public class SimpleIterator {

    public static void main(String[] args) {

        int[] arr = {10, 20, 30, 40};

        MyCollection collection = new MyCollection(arr);

        MyIterator it = collection.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}