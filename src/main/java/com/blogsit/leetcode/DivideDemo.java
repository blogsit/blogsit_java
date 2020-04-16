package com.blogsit.leetcode;

public class DivideDemo {
    public static final int INT_MIN = -2147483648;

    public static void main(String[] args) {
        //获取当前堆的大小 byte 单位
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println(heapSize);

        int a = 1111;
        int b = 100;

        //获取当前堆的大小 byte 单位
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        System.out.println(heapMaxSize);
        try {
            System.out.println(divide(a, b));
        } catch (Error ex) {
            ex.printStackTrace();
            System.out.println("异常了看不出来啊！");
        }
        int[] nums = {1, 2, 3, 4, 4, 5};
        System.out.println(removeDuplicates(nums));
    }

    public static int divide(int dividend, int divisor) {
        if (dividend == 0) {
            return 0;
        }
        if (divisor == 1) {
            return dividend;
        }
        if (divisor == -1) {
            return -dividend;
        }
        int a = dividend;
        int b = divisor;
        int sign = 1;
        if ((a > 0 && b < 0) || (a < 0 && b > 0)) {
            sign = -1;
        }
        a = a > 0 ? a : -a;
        b = b > 0 ? b : -b;
        int result = div(a, b);
        if (sign > 0) {
            return result > INT_MIN ? result : INT_MIN;
        }
        return -result;
    }

    public static int div(int a, int b) {
        if (a < b) {
            return 0;
        }
        int count = 1;
        int tb = b;
        while ((tb + tb) <= a && tb + tb < 0) {
            count = count + count;
            tb = tb + tb;
        }
        return count + div(a - tb, b);
    }

    /**
     * 移除有序的队列的重复项
     *
     * @param nums
     * @return
     */
    public static int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int p = 0;
        int q = 1;
        while (q < nums.length) {
            if (nums[p] != nums[q]) {
                if(q - p > 1){
                    nums[p + 1] = nums[q];
                }
                p++;
            }
            q++;
        }
        return p + 1;
    }
}
