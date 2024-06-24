package org.example;

import parcs.AMInfo;
import parcs.channel;
import parcs.point;
import parcs.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ParcsJob {
    public static void main(String[] args) {
        try {
            String serverFilePath = "server";
            System.out.println("Attempting to read server file from path: " + serverFilePath);
            String serverIP = Files.readString(Paths.get(serverFilePath)).trim();
            System.out.println("Server IP read from file: " + serverIP);

            System.out.println("Creating task...");
            task curtask = new task();
            curtask.addJarFile("ShellSort.jar");
            AMInfo info = new AMInfo(curtask, null);

            int[] array = generateRandomArray(100000);
            int numberOfWorkers = 2;

            long startTime = System.currentTimeMillis();
            int[] sortedArray = parallelShellSort(info, array, numberOfWorkers);
            long endTime = System.currentTimeMillis();

            System.out.println("Sorted array (first 20 elements): ");
            printArray(sortedArray, 20);

            System.out.println("Array length: " + sortedArray.length);
            System.out.println("Sorting took " + (endTime - startTime) + " milliseconds.");

            curtask.end();
            System.out.println("Task ended.");
        } catch (IOException e) {
            System.err.println("Error reading server file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(10000) - 5000;
        }
        return array;
    }

    private static int[] parallelShellSort(AMInfo info, int[] array, int numberOfWorkers) throws Exception {
        int n = array.length;
        int batchSize = n / numberOfWorkers;
        List<channel> channels = new ArrayList<>();

        System.out.println("batchSize = " + batchSize);
        for (int index = 0; index < numberOfWorkers; ++index) {
            int rangeStart = index * batchSize;
            int rangeEnd = (index + 1) * batchSize;

            if (index == numberOfWorkers - 1) {
                rangeEnd = n;
            }

            System.out.println("creating point for range [" + rangeStart + ", " + rangeEnd + "]");
            point point = info.createPoint();
            System.out.println("creating channel");
            channel channel = point.createChannel();
            channels.add(channel);

            System.out.println("executing point");
            point.execute(ShellSort.class.getCanonicalName());

            System.out.println("writing to channel");
            channel.write(Arrays.copyOfRange(array, rangeStart, rangeEnd));
        }

        System.out.println("waiting for results");
        List<int[]> sortedSubarrays = new ArrayList<>();
        for (channel ch : channels) {
            sortedSubarrays.add((int[]) ch.readObject());
        }

        return mergeSortedArrays(sortedSubarrays);
    }

    private static int[] mergeSortedArrays(List<int[]> sortedSubarrays) {
        return sortedSubarrays.stream().flatMapToInt(Arrays::stream).toArray();
    }

    private static void printArray(int[] array, int limit) {
        for (int i = 0; i < Math.min(array.length, limit); i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
}
