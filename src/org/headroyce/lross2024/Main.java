package org.headroyce.lross2024;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Handles command line arguments and reading/writing to files
 */
public class Main {
    /**
     * executable main method
     * @param args terminal arguments (expected: "-o", output text file, input text file)
     */
    public static void main (String[] args) {
        StringBuilder text = new StringBuilder("");

        if (args.length != 3) throw new Error("invalid usage. correct command usage is:\n\"-o <output data file> <input text file>\" for encoding\n\"-d <output text file> <input data file>\" for decoding");
        String input_file_type = args[2].substring(args[2].length() - 4);
        String output_file_type = args[1].substring(args[1].length() - 4);

        if (args[0].equals("-o") && input_file_type.equals(".txt") && output_file_type.equals(".dat")) {
            File file = new File(args[2]);
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    //"s" is every single character in the input
                    text.append(str).append('\n');

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Huffman_Compressor compress = new Huffman_Compressor();
            byte[] data = compress.encode(text.toString());

            //now we make a new text file
            File output = new File(args[1]);
            try {
                output.createNewFile();
                try {
                    OutputStream os = new FileOutputStream(output);
                    os.write(data);

                    os.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (args[0].equals("-d") && input_file_type.equals(".dat") && output_file_type.equals(".txt")){
            File file = new File(args[2]);
            byte[] bytes;
            String data = "";
            try {
                InputStream is = new ByteArrayInputStream(new FileInputStream(file).readAllBytes());
                bytes = is.readAllBytes();
                Huffman_Compressor decompress = new Huffman_Compressor();
                data = decompress.decode(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (data.length() > 1){
                //now we make a new text file
                File output = new File(args[1]);
                try {
                    output.createNewFile();
                    try {
                        PrintWriter pw = new PrintWriter(output);
                        pw.print(data);
                        pw.close();
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new Error("invalid usage. correct command usage is \"-o <output text file> <input text file>\"");
        }
    }
}
