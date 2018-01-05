package com.lwb.test;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * @author Liuweibo
 * @version Id: ForkJoinDemo.java, v0.1 2018/1/5 Liuweibo Exp $$
 * 当计算量较大的时候，fork/join框架能体现出优势，但是大部分情况较单线程更慢
 */
public class ForkJoinDemo {
    @Test
    public void test02() {
        long sum =0;
        long start = System.currentTimeMillis();
        for (long i=0; i<100000000000L; i++) {
            sum += i;
        }
        System.out.println(sum);
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test01() {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(1, 100000000000L);
        Future<Integer> future = forkJoinPool.submit(countTask);
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}

class CountTask extends RecursiveTask {

    private final long THRESHOLD = 1000000000L;

    private long start;

    private long end;

    public CountTask(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Object compute() {

        long sum = 0;
        // 判断任务是否还可以拆分
        boolean canCompute = (end - start) <= THRESHOLD;
        if (canCompute) {
            for (long i=start; i<=end; i++) {
                sum += i;
            }
        } else { // 还可以拆分的话，继续拆分
            long middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            // 拆分之后调用fork方法，该方法会自动调用compute方法继续判断任务是否还可以进行拆分
            leftTask.fork();
            rightTask.fork();

            // 子任务结束合并任务结果
            long leftRs = (long) leftTask.join();
            long rightRs = (long) rightTask.join();

            // 结果
            sum = leftRs + rightRs;
        }

        return sum;
    }
}














