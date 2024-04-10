package com.rolandas;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

public class Main {

  public static int X_SIZE = 29;
  public static int Y_SIZE = 29;

  public static void main(String[] args) throws InterruptedException {
    List<Flee> flees = new ArrayList<>();

    for (int i = 0; i <= X_SIZE; i++) {
      for (int j = 0; j <= Y_SIZE; j++) {
        flees.add(new Flee(i, j));
      }
    }

    ExecutorService executorService = Executors.newScheduledThreadPool(3000);

    AtomicInteger emptySquaresSum = new AtomicInteger(0);

    for (int j = 0; j < 3000; j++) {
      final int index = j;
      executorService.submit(() -> {
        try {
//          System.out.printf("Thread %s start%n", index);
          Random random = new Random();
          List<Flee> localCopy = new ArrayList<>();
          for (Flee copy: flees) {
            localCopy.add(new Flee(copy.getX(), copy.getY()));
          }
          for (int i = 0; i < 50; i++) {
            ring(localCopy, random);
          }
          int threadAverage = calculateAverageEmptySquares(localCopy);
          emptySquaresSum.addAndGet(threadAverage);
        } catch (Exception ex) {
          ex.printStackTrace();
        }

      });

    }

    executorService.shutdown();
    System.out.printf("Run average: %s", emptySquaresSum.get()/ 3000);
  }


  public static void ring(List<Flee> flees, Random random) {
    for (Flee flee : flees) {
      flee.ring(random);
    }
  }

  public static int calculateAverageEmptySquares(List<Flee> flees) {
    int[][] grid = new int[X_SIZE + 1][Y_SIZE + 1];
    for (Flee flee: flees) {
      int currentFlees = grid[flee.getX()][flee.getY()];
      grid[flee.getX()][flee.getY()] = currentFlees + 1;
    }
    int emptySquares = 0;
    for (int i = 0; i <= X_SIZE; i++) {
//      System.out.printf("[");
      for (int j = 0; j <= Y_SIZE; j++) {
//        System.out.printf("%s ", grid[i][j]);
        if (grid[i][j] == 0) {
          emptySquares++;
        }
      }
//      System.out.printf("]%n");
    }
//    System.out.printf("Empty squares: %s", emptySquares);
    return emptySquares;
  }
}


class Flee {
  int x;
  int y;

  public Flee(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void ring(Random random) {
    List<Flee> options = new ArrayList<>();
    if (x == 0) {
      options.add(new Flee(x + 1, y));
    } else if(x == Main.X_SIZE) {
      options.add(new Flee(x - 1, y));
    } else {
      options.add(new Flee(x - 1, y));
      options.add(new Flee(x + 1, y));
    }

    if (y == 0) {
      options.add(new Flee(x, y + 1));
    } else if(y == Main.Y_SIZE) {
      options.add(new Flee(x, y - 1));
    } else {
      options.add(new Flee(x, y + 1));
      options.add(new Flee(x, y - 1));
    }

    int chosenOption = random.nextInt(options.size());
    Flee jumpOption = options.get(chosenOption);
    this.x = jumpOption.x;
    this.y = jumpOption.y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Flee flee = (Flee) o;
    return x == flee.x && y == flee.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
