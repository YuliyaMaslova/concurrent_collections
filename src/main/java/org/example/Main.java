package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
  private static final int QUEUE_CAPACITY = 100;
  private static final BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
  private static final BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
  private static final BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

  public static void main(String[] args) throws InterruptedException {

    Thread textGenerator = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        String text = generateText("abc", 100000);
        try {
          aQueue.put(text);
          bQueue.put(text);
          cQueue.put(text);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

      try {
        aQueue.put("");
        bQueue.put("");
        cQueue.put("");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    textGenerator.start();

    Thread aThread = new Thread(() -> analyzeQueue(aQueue, 'a'));
    Thread bThread = new Thread(() -> analyzeQueue(bQueue, 'b'));
    Thread cThread = new Thread(() -> analyzeQueue(cQueue, 'c'));
    aThread.start();
    bThread.start();
    cThread.start();


    textGenerator.join();


    aThread.interrupt();
    bThread.interrupt();
    cThread.interrupt();
    aThread.join();
    bThread.join();
    cThread.join();
  }

  private static String generateText(String letters, int length) {
    Random random = new Random();
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
      text.append(letters.charAt(random.nextInt(letters.length())));
    }
    return text.toString();
  }

  private static void analyzeQueue(BlockingQueue<String> queue, char symbol) {
    int maxCount = 0;
    String maxText = "";
    try {
      while (true) {
        String text = queue.take();
        if (text.isEmpty()) {
          break;
        }
        int count = countSymbol(text, symbol);
        if (count > maxCount) {
          maxCount = count;
          maxText = text;
          System.out.println("New max count for symbol " + symbol + ": " + maxCount);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    System.out.println("Max count for symbol " + symbol + ": " + maxCount);
    System.out.println("Text with max count for symbol " + symbol + ": " + maxText.substring(0, 100));
  }

  private static int countSymbol(String text, char symbol) {
    int count = 0;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == symbol) {
        count++;
      }
    }
    return count;
  }
}