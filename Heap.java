import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

// Heap class used to balance the tree
class Heap {
    public ArrayList<Word> words; // List of words in the heap
    private boolean isMax; // Indicates if heap is max or min
    private int axis; // Axis at which we are making the heap, used for comparing words

    public Heap(boolean isMax, int axis) {
        this.isMax = isMax;
        this.axis = axis;
        this.words = new ArrayList<>();
        this.words.add(null); // Wasted space for ease of computation later
    }

    // Add a word to the heap then heapafy
    public void add(Word word) {
        words.add(word);
        heapifyUp(words.size() - 1);
    }

    // Remove the top word then heapafy down
    // Returns the original top element
    public Word removeTop() {
        if (words.size() <= 1) { // Heap is empty
            return null; 
        }

        Word top = words.get(1); // Word to store the top to be returned

        if (words.size() == 2) { // Heap has only one word to remove
            return words.remove(1);
        }

        // Otherwise we replace the first element in the list with the last and swim it down
        words.set(1, words.remove(words.size() - 1)); // Reassigns the value of the top
        heapifyDown(1); //Heapafy down from the first element
        return top;
    }

    // Heapafy from specified index upwards
    // Used when a word has been added to the heap
    private void heapifyUp(int index) {
        while (index > 1) {
            int parentIndex = index / 2; // Finds parent index

            // If parent index and child index should be swapped, then swap them
            if (shouldSwap(words.get(parentIndex), words.get(index))) {
                Collections.swap(words, parentIndex, index);
                index = parentIndex; // Updates index to continue through loop
            }

            // Otherwise they shouldn't be swapped and so there is nothing left to do
            else {
                break;
            }
        }
    }

    // Heapafy from specific index downwards
    // Used when a word has been removed
    private void heapifyDown(int index) {
        while (index * 2 < words.size()) {

            // Finds index of both children of the word that has been moved to top
            int leftChild = index * 2;
            int rightChild = leftChild + 1;

            // Finds the index of the child that we are interested in swapping with
            int bestChild = leftChild;
            if (rightChild < words.size() && shouldSwap(words.get(leftChild), words.get(rightChild))) {
                bestChild = rightChild;
            }

            // If the new word and the best child should be swapped, swap them
            if (shouldSwap(words.get(index), words.get(bestChild))) {
                Collections.swap(words, index, bestChild);
                index = bestChild; // Updates index for the while loop
            }

            // Otherwise they shouldn't be swapped and so there is nothing left to do
            else {
                break;
            }
        }
    }

    // Method takes a parent word and a child word and returns true if they should be swapped
    // depending on if they are in a max heap or a min heap
    private boolean shouldSwap(Word parent, Word child) {

        // Finds value of the words at the axis of the heap
        double parentValue = parent.getVector().get(axis); 
        double childValue = child.getVector().get(axis);

        // If the heap is a max heap then returns true if child is greater than parent
        // If heap is a min heap returns true if child is less then parent
        return isMax ? parentValue < childValue : parentValue > childValue;
    }
}





