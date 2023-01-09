package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    private static final int THREAD_POOL_LIMIT = 4;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_LIMIT);
        List<Future<Integer>> futureList = new ArrayList<>();
        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            Future<Integer> taskResult = executorService.submit(task);
            futureList.add(taskResult);
        }
        int maxLen = 0;
        for (Future<Integer> task : futureList) {
            maxLen = Math.max(maxLen, task.get());
        }
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Максимальная длина " + maxLen);
        executorService.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}