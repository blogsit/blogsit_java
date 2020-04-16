package com.blogsit.base;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Java8Tester {
    public static void main(String[] args) {
        Java8Tester tester = new Java8Tester();
        MathOperation addition = (a, b) -> a + b;
        MathOperation subtraction = (a, b) -> a - b;
        MathOperation multiOperation = (a, b) -> a * b;
        MathOperation division = (int a, int b) -> a / b;

        System.out.println("a + b = " + tester.operate(1, 2, addition));
        System.out.println("a - b = " + tester.operate(3, 2, subtraction));
        System.out.println("a * b = " + tester.operate(5, 2, multiOperation));
        System.out.println("a / b = " + tester.operate(5, 2, division));



        SayHello sayHello = message -> System.out.println("message----------"+message);
        SayHello sayHelloTWo = message -> message.isEmpty();

        sayHello.sayMessage("Runoob");
        sayHelloTWo.sayMessage("Runoob");



        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
        System.out.println("java8 新特性的编写");
        long count = strings.stream().filter(string -> string.isEmpty()).count();
        System.out.println("空字符的个数" + count);
        count = strings.stream().filter(string -> string.length() == 3).count();
        System.out.println("字符长度为3的个数" + count);

        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
        System.out.println("过滤空字符串的方法" + filtered);

        String mergeString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(","));
        System.out.println("过滤空字符串并且使用, 连接合并" + mergeString);

        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);

        List<Integer> squaresList = numbers.stream().map(i -> i * i).distinct().collect(Collectors.toList());
        System.out.println("Squares List: " + squaresList);
        List<Integer> integers = Arrays.asList(1, 2, 13, 4, 15, 6, 17, 8, 19);

        IntSummaryStatistics stats = integers.stream().mapToInt((x) -> x).summaryStatistics();
        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());
        System.out.println("随机数如下");
        Random random = new Random();
        random.ints().limit(10).sorted().forEach(System.out::println);
        count = strings.parallelStream().filter(string -> string.isEmpty()).count();
        System.out.println("空字符串的数量为: " + count);
    }

    interface MathOperation {
        int operation(int a, int b);
    }
    interface SayHello {
        void sayMessage(String message);
    }

    private int operate(int a, int b, MathOperation mathOperation) {
        return mathOperation.operation(a, b);
    }
}
