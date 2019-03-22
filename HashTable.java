// Name: Chenhao Lu
// Assignment: p3b Performance Analysis
// Due Date: 2019/3/14
// Lecture 001
// Email: clu92@wisc.edu

import java.util.ArrayList;

// Collision resolution scheme: array of arraylist. When threshold is reached, the size of the array
// will be doubled and incremented by one to keep it as an odd numbered size. All elements in the
// array will be rehashed based on the new table size.

/**
 * Represents a hash table
 * 
 * @author Chenhao Lu
 *
 * @param <K>
 * @param <V>
 */
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {

  /**
   * Inner class, represents a node in the hash table
   * 
   * @author Chenhao Lu
   *
   * @param <K>
   * @param <V>
   */
  private class HashNode<K, V> {
    private K key;
    private V value;
    private HashNode<K, V> next; // fields and references of a hash node

    /**
     * Constructor that initializes a hash node
     * 
     * @param key key of the node
     * @param value value of the node
     */
    private HashNode(K key, V value) {
      this.key = key;
      this.value = value;
      next = null;
    }

    /**
     * Getter method for key
     * 
     * @return key
     */
    private K getKey() {
      return key;
    }

    /**
     * Getter method for value
     * 
     * @return value
     */
    private V getValue() {
      return value;
    }

    /**
     * Getter method for next reference
     * 
     * @return next
     */
    private HashNode<K, V> getNext() {
      return next;
    }

    /**
     * Setter method for next reference
     * 
     * @param node the node that is next to the current node
     */
    private void setNext(HashNode<K, V> node) {
      next = node;
    }
  }

  // TODO: ADD and comment DATA FIELD MEMBERS needed for your implementation
  private ArrayList table[];
  private int tableSize;
  private int numItems;
  private double loadFactor;
  private double loadFactorThreshold; // fields of the hash table

  /**
   * No-arg constructor that initializes a default hash table.
   */
  public HashTable() {
    tableSize = 11;
    table = new ArrayList[tableSize]; // initial capacity of 11
    for (int i = 0; i < table.length; i++)
      table[i] = new ArrayList<HashNode<K, V>>(); // initialize with an empty arraylist
    numItems = 0;
    loadFactor = 0;
    loadFactorThreshold = 0.75; // threshold of 0.75
  }

  /**
   * Constructor that initializes a hash table with given capacity and threshold
   * 
   * @param initialCapacity initial capacity of the hash table
   * @param loadFactorThreshold the load factor that causes a resize and rehash
   */
  public HashTable(int initialCapacity, double loadFactorThreshold) {
    this.tableSize = initialCapacity;
    table = new ArrayList[tableSize];
    for (int i = 0; i < table.length; i++)
      table[i] = new ArrayList<HashNode<K, V>>(); // initialize with an empty arraylist
    numItems = 0;
    loadFactor = (double) numItems / tableSize;
    this.loadFactorThreshold = loadFactorThreshold;
  }

  /**
   * Add the key,value pair to the data structure and increase the number of keys.
   */
  @Override
  public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {
    if (key == null)
      throw new IllegalNullKeyException(); // throw exception when inserting a null key

    HashNode<K, V> node = new HashNode<K, V>(key, value);
    int hashCode = Math.abs(key.hashCode());
    int hashIndex = hashCode % tableSize; // calculate the hash index

    ArrayList list = table[hashIndex];
    for (int i = 0; i < list.size(); i++) {
      HashNode<K, V> dup = (HashNode<K, V>) (list.get(i));
      if (dup.getKey().equals(key)) // check for duplicates in the arraylist of the hashIndex
        throw new DuplicateKeyException(); // throw exception when duplicate is found
    }

    table[hashIndex].add(node); // insert the node to the corresponding array list
    numItems++;

    loadFactor = (double) numItems / tableSize; // calculate load factor after every insertion
    if (loadFactor >= loadFactorThreshold) {
      tableSize = tableSize * 2 + 1; // resize if exceeded threshold
      ArrayList temp[] = new ArrayList[tableSize]; // create the resized table
      for (int i = 0; i < tableSize; i++)
        temp[i] = new ArrayList<HashNode<K, V>>(); // initialize the new table
      for (int i = 0; i < table.length; i++) {
        ArrayList list2 = table[i];
        for (int ii = 0; ii < list2.size(); ii++) {
          HashNode<K, V> tempNode = (HashNode<K, V>) list2.get(ii);
          int hashIndex2 = Math.abs(tempNode.getKey().hashCode()) % tableSize;
          temp[hashIndex2].add(tempNode); // rehash the elements into the new table
        }
      }
      table = temp;
    }
  }

  /**
   * Remove the key-value pair from the table
   * 
   * @return true if successfully removed, false if key not found
   */
  @Override
  public boolean remove(K key) throws IllegalNullKeyException {
    if (key == null)
      throw new IllegalNullKeyException(); // throw exception when removing a null key
    int hashIndex = Math.abs(key.hashCode()) % tableSize;
    ArrayList list = table[hashIndex]; // list that is at the hashIndex of the parameter key
    for (int i = 0; i < list.size(); i++) {
      HashNode<K, V> node = (HashNode<K, V>) (list.get(i));
      K keyTemp = node.getKey();
      if (keyTemp.equals(key)) {
        list.remove(i); // remove when match is found
        numItems--;
        return true;
      }
    }
    return false; // if no match is found, return false
  }

  /**
   * Returns the value associated with the specified key
   */
  @Override
  public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
    if (key == null)
      throw new IllegalNullKeyException(); // throw exception if key is null

    int hashIndex = Math.abs(key.hashCode()) % tableSize;
    ArrayList list = table[hashIndex]; // list that contains the parameter key
    for (int i = 0; i < list.size(); i++) {
      HashNode<K, V> node = (HashNode<K, V>) (list.get(i));
      K keyTemp = node.getKey();
      if (keyTemp.equals(key)) {
        return node.getValue(); // return the value when a key match is found
      }
    }
    throw new KeyNotFoundException(); // throw exception if no such key
  }

  /**
   * Returns the number of key,value pairs in the data structure
   */
  @Override
  public int numKeys() {
    return numItems;
  }

  /**
   * Returns the load factor threshold that was passed into the constructor when creating the
   * instance of the HashTable.
   */
  @Override
  public double getLoadFactorThreshold() {
    return this.loadFactorThreshold;
  }

  /**
   * Returns the current load factor for this hash table
   */
  @Override
  public double getLoadFactor() {
    return (double) numItems / tableSize;
  }

  /**
   * Return the current Capacity (table size) of the hash table array
   */
  @Override
  public int getCapacity() {
    return tableSize;
  }

  /**
   * Returns the collision resolution scheme used for this hash table.
   */
  @Override
  public int getCollisionResolution() {
    return 4; // array of arraylist
  }
}
