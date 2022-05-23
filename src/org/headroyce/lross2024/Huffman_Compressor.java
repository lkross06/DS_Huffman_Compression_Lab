package org.headroyce.lross2024;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Object which compresses a string of characters
 */
public class Huffman_Compressor{
    /**
     * constructs a new object
     */
    public Huffman_Compressor(){
    }

    /**
     * compresses a string text and returns the Huffman dictionary for it
     * @param text string to compress
     * @return HashMap dictionary of characters and bit values
     */
    public byte[] encode(String text) {

        HashMap<String, Integer> freqTable = countFreqs(text);

        Heap<Huffman_Node> freqheap = FreqTableToHeap(freqTable);

        Huffman_Node eof = new Huffman_Node();
        eof.setChar(Character.toString((char) 7));
        eof.setFreq(1);
        freqheap.push(eof);

        Huffman_Node node_1 = null;
        Huffman_Node node_2 = null;
        while (!freqheap.isEmpty()) {
            node_1 = freqheap.pop();
            node_2 = freqheap.pop();

            if( node_2 == null ){
                break;
            }

            Huffman_Node newNode = new Huffman_Node();
            newNode.setChar(node_1.getChar() + node_2.getChar());
            newNode.setFreq(node_1.getFreq() + node_2.getFreq());
            newNode.setLeft(node_1);
            newNode.setRight(node_2);

            freqheap.push(newNode);
        }
        LinkedHashMap<String, String> dictionary =  toDictionary(node_1);
        assert node_1 != null;
        String preOrder = preOrder(node_1);
        String bits = preOrder + encodeData(dictionary, text);

        while (bits.length() % 8 != 0) bits += "0";

        return toBytes(bits);
    }

    /**
     * counts the frequencies of every character and returns a lexicographically-sorted frequency table HashMap
     * @param text string to count the frequencies from
     * @return HashMap frequency table
     */
    private HashMap<String, Integer> countFreqs( String text ){
        HashMap<String, Integer> rtn = new HashMap<>();

        Arrays.sort(text.split(""));
        for (String str : text.split("")){
            //if the character is in the dictionary, add 1 to the frequency
            if (rtn.containsKey(str)){
                rtn.replace(str, rtn.get(str), rtn.get(str) + 1);
            } else {
                //otherwise, add a new entry
                rtn.put(str, 1);
            }
        }

        return rtn;
    }

    /**
     * encodes the input data with the huffman dictionary
     * @param dictionary huffman dictionary
     * @param data data to encode
     * @return encoded data
     */
    private String encodeData(LinkedHashMap<String, String> dictionary, String data){
        String[] arr = data.split("");
        String rtn = "";
        for (String s : arr){
            rtn += dictionary.get(s);
        }
        //add the end character at the end :>
        for (String s : dictionary.keySet()) if (s.equals(Character.toString((char)7))) rtn += dictionary.get(s);
        return rtn;
    }

    /**
     * performs a preorder traversal of the huffman tree and converts it into bits
     * @param root root node
     * @return string of bits
     */
    private String preOrder(Huffman_Node root){
        ArrayList<String> arr = new ArrayList<>();
        preOrderHelper(root, arr);
        String rtn = "";
        for (String s : arr){
            rtn += s;
        }
        return rtn;
    }

    /**
     * recursive function for preorder traversal
     * @param curr current node being processed
     * @param arr array of bits to add to
     */
    private void preOrderHelper(Huffman_Node curr, ArrayList<String> arr){
        if (curr != null){
            if (curr.getLeft() != null && curr.getRight() != null){
                //internal node
                arr.add("0");
            } else {
                //leaf
                arr.add("1");
                String result = toBits(curr.getChar().getBytes(StandardCharsets.UTF_8));
                arr.add(result);
            }
            preOrderHelper(curr.left, arr);
            preOrderHelper(curr.right, arr);
        }
    }

    /**
     * converts a byte array to binary
     * @param arr array of bytes
     * @return string of binary
     */
    private static String toBits(byte[] arr) {

        String rtn = "";

        int preferedSize = 8;

        BitSet bitSet;
        HashMap<Byte, String> hash = new HashMap<>();
        //make a map of all the byte to 8-bit values
        for (int i = 0; i < (1 << preferedSize); i++) {
            bitSet = new BitSet(preferedSize);
            int count = 0;
            int temp = i;
            while (temp > 0) {
                if ((temp % 2) == 1)
                    bitSet.set(count);
                temp = temp / 2;
                count++;
            }

            StringBuffer bf = new StringBuffer();
            for (count = preferedSize - 1; count >= 0; count--)
                bf.append((bitSet.get(count) ? 1 : 0));

            byte b = (byte) i;
            hash.put(b, bf.toString());
        }
        for (byte b : arr){
            String bits = hash.get(b);
            rtn += bits;
        }
        return rtn;
    }

    /**
     * converts bits to byte array
     * @param bits bit string to convert
     * @return byte array
     */
    private byte[] toBytes(String bits){
        byte[] rtn = new byte[bits.length() / 8];
        int index = 0;
        for (int i = 0; i < bits.length(); i+=8){
            String bits2 = bits.substring(i, i+8);
            int yay = Integer.parseInt(bits2, 2);
            rtn[index] = (byte) yay;
            index++;
        }
        return rtn;
    }
    /**
     * translates a Binary Tree to a HashMap containing the bit values for each leaf node
     * (handled with recursion)
     * @param root root of the binary tree
     * @return HashMap dictionary of characters and bit values
     */
    private LinkedHashMap<String, String> toDictionary(Huffman_Node root){
        HashMap<String, String> dictionary = new HashMap<>();
        String bits = "";
        //0 for left, 1 for right
        dictionaryHelper(root, dictionary, bits);

        return sortDictionary(dictionary);
    }

    /**
     * recursive helper function to get bit values of each character in Binary Tree
     * @param curr Binary Node being focused on
     * @param dictionary HashMap to add new entries to
     * @param bits current bit string (added onto as it recurses)
     */
    private void dictionaryHelper(Huffman_Node curr, HashMap<String, String> dictionary, String bits){
        if (curr.getRight() == null && curr.getLeft() == null){
            //leaf node, record data
            dictionary.put(curr.getChar(), bits);
        }
        //left
        if (curr.getLeft() != null){
            dictionaryHelper(curr.getLeft(), dictionary, bits+"0");
        }
        //right
        if (curr.getRight() != null){
            dictionaryHelper(curr.getRight(), dictionary, bits+"1");
        }
    }

    /**
     * sorts the dictionary lexicographically and returns a linked hash map of the sorted dictionary (keeps its order)
     * @param dictionary dictionary to be sorted
     * @return linked hash map of sorted dictionary
     */
    private LinkedHashMap<String, String> sortDictionary(HashMap<String, String> dictionary){
        LinkedHashMap<String, String> sorted = new LinkedHashMap<>();
        StringBuilder chars = new StringBuilder();
        String[] sorted_chars;

        for (String s : dictionary.keySet()) chars.append(s);
        sorted_chars = chars.toString().split("");
        Arrays.sort(sorted_chars);

        for (String s : sorted_chars){
            sorted.put(s, dictionary.get(s));
        }

        return sorted;
    }

    /**
     * translates the Frequency Table to an empty heap
     * @param freqTable frequency table HashMap
     * @return completed heap
     */
    private Heap<Huffman_Node> FreqTableToHeap(HashMap<String, Integer> freqTable){
        Heap<Huffman_Node> freqheap = new Heap<>();
        for (String s : freqTable.keySet()){
            int freq = freqTable.get(s);
            Huffman_Node node = new Huffman_Node();
            node.setChar(s);
            node.setFreq(freq);
            freqheap.push(node);
        }
        return freqheap;
    }

    /**
     * recursive function for huffman tree builder
     * @param curr current node
     * @param bit_array array of bits being read from and processed
     */
    private void huffmanTreeBuilder_helper(Huffman_Node curr, ArrayList<String> bit_array ){
        String s = bit_array.remove(0);
        if (s.equals("1")){
            String char_bits = "";
            for (int j = 1; j <= 8; j++){
                char_bits += bit_array.remove(0);
            }
            int temp = Integer.parseInt(char_bits, 2);
            String character = Character.toString( (char) temp );
            curr.setChar(character);
            return;
        }

        curr.setLeft(new Huffman_Node());
        curr.setRight(new Huffman_Node());

        huffmanTreeBuilder_helper(curr.getLeft(), bit_array);
        huffmanTreeBuilder_helper(curr.getRight(), bit_array);
    }

    /**
     * decodes an array of bytes into a string
     * @param bytes byte array to decode
     * @return string of data
     */
    public String decode(byte[] bytes){
        //i couldnt figure out how to use bitset properly
        String bits = toBits(bytes);

        //let get the tree, do it here so that you can directly edit bits and get root node
        ArrayList<String> bit_array = new ArrayList<>(Arrays.asList(bits.split("")));

        bit_array.remove(0);
        Huffman_Node root = new Huffman_Node();
        root.setLeft(new Huffman_Node());
        root.setRight(new Huffman_Node());

        huffmanTreeBuilder_helper(root.getLeft(), bit_array);
        huffmanTreeBuilder_helper(root.getRight(), bit_array);

        String final_data = "";
        Huffman_Node curr = root;
        while (!bit_array.isEmpty()){
            //base case -> leaf node
            String s = bit_array.remove(0);
            if (s.equals("0")){
                curr = curr.getLeft();
            } else if (s.equals("1")){
                curr = curr.getRight();
            }
            if (curr.getRight() == null && curr.getLeft() == null){
                //encountered leaf node

                if (curr.getChar().equals(Character.toString((char) 7))){
                    break;
                }

                final_data += curr.getChar();

                curr = root;
            }

        }

        return final_data;
    }

    /**
     * Binary Nodes containing characters and frequencies
     */
    private class Huffman_Node implements Comparable<Huffman_Node> {
        private int freq;
        private String chars;
        private Huffman_Node left;
        private Huffman_Node right;

        /**
         * constructs a Binary node containing characters and frequencies, and setting its children to null (until later defined)
         */
        public Huffman_Node(){
            this.chars = null;
            this.freq = 0;
            this.left = null;
            this.right = null;
        }

        /**
         * sets right child
         * @param right right child node
         */
        public void setRight(Huffman_Node right){ this.right = right; }

        /**
         * sets left child
         * @param left left child node
         */
        public void setLeft(Huffman_Node left){ this.left = left; }

        /**
         * gets left child
         * @return left child
         */
        public Huffman_Node getLeft() {
            return left;
        }

        /**
         * gets right child
         * @return right child
         */
        public Huffman_Node getRight() {
            return right;
        }

        /**
         * accessor for frequency data
         * @return frequency data
         */
        public int getFreq(){
            return this.freq;
        }
        /**
         * accessor for character data
         * @return character data
         */
        public String getChar(){
            return this.chars;
        }

        /**
         * sets the chars attribute
         * @param chars value for attribute
         */
        public void setChar(String chars){
            this.chars = chars;
        }

        /**
         * sets the frequency attribute
         * @param freq value for attribute
         */
        public void setFreq(int freq){
            this.freq = freq;
        }

        /**
         * compares a node's data to another node by frequency (this is how the heap is sorted)
         * @param other other node to compare to
         * @return 1 if this is greater, 0 if they are equal, -1 if this is less than
         */
        public int compareTo(Huffman_Node other){
            return Integer.compare(this.freq, other.getFreq());
        }
    }


}
