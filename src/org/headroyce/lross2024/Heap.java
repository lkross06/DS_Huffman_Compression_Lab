package org.headroyce.lross2024;

import java.util.ArrayList;

/**
 * Generic Min-Heap array-based data structure
 * @param <T> data type stored in the heap
 */
public class Heap<T extends Comparable<T>> {
    private final ArrayList<T> arr;

    /**
     * constructs a new heap with an empty array
     * @time O(1)
     */
    public Heap(){
        arr = new ArrayList<>();
    }

    /**
     * pushed data into the priority queue and sorts it
     * @param data data to put into heap
     * @time O(n) (input is arraylist)
     */
    public void push(T data){
        arr.add(data);
        //if this isnt the root you can heapify when necessary
        heapify_up(arr.size() - 1);
    }

    /**
     * removes and returns the first item in the priority queue
     * @return data that is at the top of the queue
     * @time O(log n)
     */
    public T pop(){
        if (arr.isEmpty()) return null;

        T rtn = arr.get(0);
        T last = arr.remove(arr.size()-1);

        if( !arr.isEmpty() ){
            arr.set(0, last);
            heapify_down(0);
        }
        return rtn;
    }

    /**
     * returns the value at the top of the queue
     * @return data that is at the top of the queue
     * @time O(1)
     */
    public T peek(){
        return arr.get(0);
    }

    /**
     * checks if the array is empty
     * @return true if the array has no elements, false otherwise
     * @time O(1)
     */
    public boolean isEmpty(){
        return arr.isEmpty();
    }

    /**
     * sorts newly-added data into its correct position on the priority queue
     * (handled with iteration)
     * @param child_index index of newly-added data
     * @time O(n) (input is arraylist)
     */
    private void heapify_up(int child_index){
        int i = child_index;
        while (i >= 0 && i < arr.size()){
            int parent_index = (i - 1) / 2;
            if (parent_index >= 0 && arr.get(parent_index).compareTo(arr.get(i)) > 0){
                T tmp = arr.get(i);
                arr.set(i, arr.get(parent_index));
                arr.set(parent_index, tmp);

                i = parent_index;
            } else {
                break;
            }
        }
    }

    /**
     * re-sorts the data at the bottom of the priority queue back into the heap
     * (handled with recursion)
     * @param parent_index index of current data to analyze
     * @time O(log n)
     */
    private void heapify_down(int parent_index){

        int left_index = (parent_index * 2) + 1;
        int right_index = (parent_index * 2) + 2;
        int smaller = parent_index;
        if (left_index < arr.size()) if (arr.get(left_index).compareTo(arr.get(smaller)) <= 0){
            smaller = left_index;
        }
        if (right_index < arr.size()) if (arr.get(right_index).compareTo(arr.get(smaller)) < 0){
            //parent is greater than one of its children (at least), swap
            smaller = right_index;
        }

        if( smaller != parent_index){
            // swap
            T tmp = arr.get(smaller);
            arr.set(smaller, arr.get(parent_index));
            arr.set(parent_index, tmp);

            heapify_down(smaller);
        }
    }
}
