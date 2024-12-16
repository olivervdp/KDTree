# KDTree
Implementation of a balanced KDTree using heaps that can find nearest neighbor.

Major classes:
Word
Heap
KDTree

Word class:
	A word object is a word and its associated vector. These are the objects that will populate the KDTree
Its constructor takes a string, separates it into individual words, and sets the word attribute to the first word in the string and puts the rest of the words in an arraylist.
	It has getWord and getVector methods which return the corresponding attribute. It also has a compareTo method which compares the word to another given the axis at which to compare.
	Also in this class is the static method readWordsFromFile which takes a file and returns an arraylist of the words which have been collected from the file.
	To verify the correctness of Word, I took the test file and turned it into a list of words using readWordsFromFile and printed them to verify all was well.

Heap class:
	A heap object used for balancing the KDTree. The object’s attributes are a list of words, a boolean isMax and an int axis. The boolean represents if the heap is a max heap or a min heap, both min heaps and max heaps are needed to find the median in the KDTree. The axis tells the heap how to compare elements.
	The class has an add method which adds a word at the end of the list of words then heapifies the list. It also has a remove top method which removes the top then heapifies what remains. For each of those methods there is a heapify method. For adding we heapify the element which was added and swim it up the tree. For removing, we heapify from the top by moving the last indexed element in words to the top, then sinking it down.
Within each for the sinking and swimming, we must compare parent and child words. To do this we use a method shouldSwap which returns a boolean. This method uses the axis to compare words and then uses isMax to say if the words should be swapped or not.
To verify the correctness of Heap class, I tested the heaps with a simple data set of a few words with two dimensional vectors. 

KDTree class:
  Uses KDNodes which are an object with a word and a left and right KDNode.
A KDTree object is a tree of words sorted by the KDTree property. It has two attributes, its root and its dimension. The dimension is the dimension of the vectors in the words.
  Its constructor takes a list of words and sets the dimension by checking one of the words, it also sets the root using the buildTree method. The buildTree method is a recursive method which returns a KDNode. It takes a list of words and, using the method, heapPartition, partitions the words into two heaps, one with all words less than or equal to the median, and one with all words greater than the median. From here, we can set the word of the KDNode to be the median and recursively set the children nodes using the words in the two heaps.
  We use heapPartition here to make a balanced tree. Since each node’s word is the median of all words in its subtree, there will be an equal number of words in the left and right subtree.
  The heapPartition method takes a list of words and the axis that the heap is located. It created a max heap and a min heap. It then, adds words from the list of words to the heaps, keeping the heaps the same size and keeping the words partitioned by their value at the axis. Two heaps are returned, one with the smaller words and the other with the larger words.

  The KDTree also has a nearestNeighbor method. This is a recursive method which returns the nearest neighbor from a given list of words to an input word. It recursively searches through the tree for the leaf that is closest to the input value. It stores this node then goes back up the tree, checking if, at each node, the branch that was not searched could have a word that is closer. If so, it recursively checks this branch to see if any nodes in it are better than the one already found. Returns the node that is closest to the input.
  In the nearestNeighbor method there is a closerDistance method which takes two nodes and the target vector and returns the node that is closest to the target. This uses a method, distance, which returns the square of the euclidean norm of two vectors. We can use the square of the norm since we are only interested in which distance is smaller, not their actual values.

  The main method takes the input txt file and creates a list of the words within it. It then loops through the stream of test words and prints the nearest neighbor of each of the test words.

  To start verifying the correctness of the tree class, I tested the buildTree method on small data sets. I checked that the tree satisfied the KDTree properties manually by, while creating the tree, printing nodes and their child nodes. 
  I tested the nearest neighbor method manually as well. One test was with a small dataset that could easily be verified. I also tested it by having the system.in words be the same as the words in the tree so for each word the nearest word was itself.
  
