/**
 * KDTree implementation.
 * 
 * Used to store words with associated vectors
 * and find the nearest neighbors of the words.
 * Impliments heaps in the creation of the balenced tree.
 * 
 * @author Oliver Vandeploeg
 * 
 * 
 * Collaboration Statement: I worked alone on this
 * assignment with assistance from 
 *      Oracle’s standard Java documentation,
 *      Wikipedia's KDTree page: https://en.wikipedia.org/wiki/K-d_tree,
 *      Stable Sort's youtube video on KDTrees: https://www.youtube.com/watch?v=Glp7THUpGow,
 *      w3schools coding website,
 *      geeks for geeks coding website,
 *      ChatGPT.
 * 
 * 
 * Compiles with
 * 
 * javac Kdnn.java
 * 
 * Runs with:
 * cat test.txt | java Kdnn input.txt
 * 
 * Where test is a txt file of just words and their vectors, seperated by new lines
 * and input is a txt file of words and their vectors seperated by new line
 * where the first line is not a word
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Class to represent a node in the tree
class KDNode {
    Word word;
    KDNode left;
    KDNode right;

    public KDNode(Word word) {
        this.word = word;
    }
}


public class Kdnn {
    private KDNode root;  // Root node of the tree
    private int dimensions; // Dimension of the tree defined as the length of the array in each word

    // Constructor takes a list of words and builds a balanced KD tree
    public Kdnn(List<Word> words) {
        this.dimensions = words.get(0).getVector().size(); // Sets the dimension to the length of the word arrays
        this.root = buildTree(words, 0); // Builds the tree begining at the root
    }

    // Recursively builds the KD Tree given a list of words and the depth in the tree
    private KDNode buildTree(List<Word> words, int depth) {
        if (words.isEmpty()) { // Base case where there are no more words to add to the tree
            return null;
        }

        int axis = depth % dimensions; // Used to denote which index is being compared at this depth
        
        // Two heaps that partition the words into the top half and the bottom half
        List<Heap> heaps = heapPartition(words, axis);

        Heap maxHeap = heaps.get(0); // Lower half of the words
        Heap minHeap = heaps.get(1); // Upper half of the words
        Word median = maxHeap.removeTop(); // Removes the median from maxheap and returns it

        // Stores the two seperate lists of words to be added to their respective subtrees
        List<Word> leftWords = new ArrayList<>(maxHeap.words.subList(1, maxHeap.words.size()));
        List<Word> rightWords = new ArrayList<>(minHeap.words.subList(1, minHeap.words.size()));

        // Defines word in the node to be the median word
        KDNode node = new KDNode(median);

        // Recursive call to build the subtrees of node
        node.left = buildTree(leftWords, depth + 1);
        node.right = buildTree(rightWords, depth + 1);

        return node; // Returns the root node
    }


    // Returns two heaps, one with the lower half of the words and the other with the rest
    // Where the top of the max heap is the median
    // Used for balancing the tree
    private List<Heap> heapPartition(List<Word> words, int axis) {
        
        // Two heaps, one max heap and one min heap, that hold the words 
        Heap maxHeap = new Heap(true, axis);
        Heap minHeap = new Heap(false, axis);

        for (Word word : words) { // For each word in words
            maxHeap.add(word); // Add the word to the maxheap

            // If the heaps are uneven, we rebalance
            // Where max heap always has more or the same number of elements as minheap
            // This lets us know that the median will always be the top of maxheap
            if (maxHeap.words.size() > minHeap.words.size() + 1) {
                minHeap.add(maxHeap.removeTop());
            }


            
            // If we did not need to rebalance, we must check if the heaps still strictly seperate the words 
            // i.e. the top of min heap is greater than the top of max heap
            else if (minHeap.words.size() > 1){
                if(minHeap.words.get(1).getVector().get(axis) < maxHeap.words.get(1).getVector().get(axis)) {
                
                    // Swap the tops of the stacks
                    Word temp = minHeap.removeTop();
                    minHeap.add(maxHeap.removeTop());
                    maxHeap.add(temp);
                }
            }       
        }

        // Makes a list to store both lists of words, one for each heap
        List<Heap> heaps = new ArrayList<>();
        heaps.add(maxHeap);
        heaps.add(minHeap);
        return heaps;
    }


    // Search for the nearest neighbor of a given target vector
    public Word nearestNeighbor(List<Double> target) {

        // Used in debugging
        // Checking that the input vector is valid to search for
        if (target.size() != dimensions) {
            System.out.print("target size" + target.size());
            System.out.print("dimension"+ dimensions);
            throw new IllegalArgumentException("Target vector must have " + dimensions + " dimensions.");
        }

        // Calls function to do the work of finding the nearest neighbor
        return nearestNeighbor(root, target, 0).word;
    }

    // Recursive function for finding nearest neighbor
    // Takes a node that is the root of the tree,
    // The vector being searched for,
    // And the depth that the root is at
    // Returns the root node
    private KDNode nearestNeighbor(KDNode node, List<Double> target, int depth) {
        
        // Base case where we are as far down the tree as possible
        if (node == null) {
            return null;
        }

        // Used to denote which index is being compared at this depth
        int axis = depth % dimensions;

        // Sets next branch to the correct subtree of node to be recursively searched
        // Stores other branch to be checked when uncertain that best has been found in next branch
        KDNode nextBranch = target.get(axis) < node.word.getVector().get(axis) ? node.left : node.right;
        KDNode otherBranch = target.get(axis) < node.word.getVector().get(axis) ? node.right : node.left;

        // Recursive call returns the node that is closest to target 
        // Once nextBranch is null, we have reached the bottom of the tree and
        // nearest neighbor will return null. Thus closer distance returns the leaf
        KDNode best = closerDistance(target, nearestNeighbor(nextBranch, target, depth + 1), node);

        // Now, coming back up the tree,
        // either best is truly the best, or there is some node that is in otherbranch
        // which could possibly be closer than best

        // Checks if it is possible for a node in this branch to be closer
        double distanceToBoundary = Math.abs(target.get(axis) - node.word.getVector().get(axis));
        if (distanceToBoundary < distance(target, best.word.getVector())) {

            // If it is possible, then checks all nodes in this subtree and updates best
            best = closerDistance(target, nearestNeighbor(otherBranch, target, depth + 1), best);
        }

        // Returns the best node found so far
        return best;
    }

    // Takes the target vector and two other nodes and returns
    // the node with the vector that is closest to the target
    private KDNode closerDistance(List<Double> target, KDNode a, KDNode b) {
        
        // If one is null, we say that the other is closer
        // This makes the base case in nearest neighbor work
        if (a == null) return b;
        if (b == null) return a;

        // Calcluates the distance from target to a and from target to b
        double distanceA = distance(target, a.word.getVector());
        double distanceB = distance(target, b.word.getVector());

        // Returns the node associated with the smallest distance
        return distanceA < distanceB ? a : b;
    }

    // Returns the square of the magnitude of the vector a - b
    // Returns square to avoid taking sqrt, this is okay since we just care about comparing distances
    private static double distance(List<Double> a, List<Double> b) {
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            double diff = a.get(i) - b.get(i);
            sum += diff * diff;
        }
        return sum;
    }


    public static void main(String[] args) {
        String dataFile = args[0];

        try {
            // Load data from the file to build the KD tree
            List<Word> words = Word.readWordsFromFile(dataFile);
            words.remove(0); // Removes the first line which just gives the number of words and the dimension 

            // For debugging
            /*
            for (Word w: words){
                System.out.println(w.getWord() + w.getVector());
            }
            */

            // Creates the tree out of the list of words
            Kdnn kdTree = new Kdnn(words);

            // Read test input from System.in
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line; // Line to be iterated over

            // Iterates over all lines in the input file
            while ((line = reader.readLine()) != null) { 
                Word testWord = new Word(line); // Makes a word object out of the line

                // Finds nearest neighbor to the test word
                Word nearest = kdTree.nearestNeighbor(testWord.getVector());

                // Calculates distance from nearest to testWord to be printed
                double distance = Math.sqrt(distance(testWord.getVector(), nearest.getVector()));

                System.out.println(testWord.getWord() + " nearest " + nearest.getWord() + " dist = " + distance);
                
                // For debugging
                /*
                boolean isSame = true;
                for (int i = 0; i < testWord.getVector().size(); i++){
                    if (testWord.getWord().compareTo(nearest.getWord()) != 0){
                        isSame = false;
                    }
                }
                    System.out.println(testWord.getWord() + " nearest " + nearest.getWord() + " dist = " + distance + isSame);
                
                */
                
            }
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}














