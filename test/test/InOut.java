package test;

import java.io.*;
public class InOut {
 
  public static void main(String[] args) throws IOException {
    PipedInputStream pin = new PipedInputStream();
    PipedOutputStream pout = new PipedOutputStream(pin);
 
    PrintStream out = new PrintStream(pout);
    BufferedReader in = new BufferedReader(new InputStreamReader(pin));
 
    System.out.println("Writing to output stream...");
    out.println("Hello World!");
    out.flush();
 
    System.out.println("Text written: " + in.readLine());
  }
 
}

