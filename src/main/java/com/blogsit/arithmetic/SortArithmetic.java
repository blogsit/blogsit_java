package com.blogsit.arithmetic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Sort Arithmetic  by  blogsit
 * 注意点：1、稳定排序和非稳定排序的区别：如果a在b之前，当A=B时  a可能会出现在b后面就是非稳定排序。
 * 2、对排序数据的总的操作次数反映当Ñ变化时，操作次数呈现什么规律。
 * 3、是指算法在计算机内执行时所需存储空间的度量，它也是数据规模Ñ的函数。
 */
public class SortArithmetic {
    /**
     * 冒泡排序
     *
     * @param input
     * @return
     */
    public Integer[] bubbleSort(Integer[] input) {
        int midTemp = 0;
        int length = input.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length - 1 - i; j++) {
                if (input[j] > input[j + 1]) {//相邻元素两个对比
                    midTemp = input[j + 1];
                    input[j + 1] = input[j];
                    input[j] = midTemp;
                }
            }
        }
        return input;
    }

    /**
     * 选择排序
     *
     * @param inputArr
     * @return
     */
    public Integer[] selectSort(Integer[] inputArr) {
        int length = inputArr.length;
        int minIndex = 0;//最小数的索引
        int temp = 0;//中间值
        for (int i = 0; i < length - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < length; j++) {
                if (inputArr[j] < inputArr[minIndex]) {//寻找最小数
                    minIndex = j;//将最小数的索引保存
                }
            }
            temp = inputArr[i];
            inputArr[i] = inputArr[minIndex];
            inputArr[minIndex] = temp;
        }
        return inputArr;
    }

    /**
     * 插入排序
     *
     * @param input
     */
    public Integer[] insertArray(Integer[] input) {
        int length = input.length;
        int preIndex = 0;
        int current = 0;
        for (int i = 1; i < length; i++) {
            preIndex = i - 1;
            current = input[i];
            while (preIndex >= 0 && input[preIndex] > current) {
                input[preIndex + 1] = input[preIndex];
                preIndex--;
            }
            input[preIndex + 1] = current;
        }
        return input;
    }

    public static void main(String[] args) {
        List<String> strings  = Arrays.asList("2","4","6","2","3","1");
        Collections.sort(strings);
        for (String i: strings) {
            System.out.println(i);
        }

    }

}
