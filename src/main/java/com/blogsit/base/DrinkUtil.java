package com.blogsit.base;

/**
 *
 * //评测题目: 现有n瓶啤酒，每3个空瓶子换一瓶啤酒，每7个瓶盖子也可以换一瓶啤酒， 一个瓶子有一个盖子
 * //问最后可以喝多少瓶啤酒?（用Java实现完整可执行的代码，15min）
 * DrinkUtil类
 * 喝了 - 空瓶子(剩) - 盖子(剩) >> 兑换：总数 = 空瓶子(兑换) + 盖子(兑换)
 * 100 - 0 - 0 >> 兑换：47 = 33(=100/3) + 14(=100/7)
 * 47 - 1 - 2 >> 兑换：23 = 16(=48/3) + 7(=49/7)
 * 23 - 0 - 0 >> 兑换：10 = 7(=23/3) + 3(=23/7)
 * 10 - 2 - 2 >> 兑换：5 = 4(=12/3) + 1(=12/7)
 * 5 - 0 - 5 >> 兑换：2 = 1(=5/3) + 1(=10/7)
 * 2 - 2 - 3 >> 兑换：1 = 1(=4/3) + 0(=5/7)
 * 1 - 1 - 5 >> 兑换：0 = 0(=2/3) + 0(=6/7)
 */
public class DrinkUtil {
    //最大计算值
    public final static long MAX_VALUE = 100000000L;

    /**
     * @param inputNum  有盖子的瓶子
     * @param bottle    无盖子的瓶子
     * @param bottleCap 盖子
     * @return
     */
    public static long drinkCount(long inputNum, long bottle, long bottleCap) {
        //不支持负数
        if (inputNum <= 0) {
            return 0;
        }
        //设置最大计算范围，-1标识不支持
        if (inputNum >= MAX_VALUE) {
            return -1;
        }
        if (inputNum == 0 && bottleCap < 7 && bottle < 3) {
            return 0;
        }
        // 现在可以喝的数量
        long all = inputNum;

        // 喝完后瓶身的数量
        bottle += all;
        // 喝完后瓶盖的数量
        bottleCap += all;
        // 喝完后瓶身和瓶盖可以换的整瓶酒
        long newBottle = bottle / 3 + bottleCap / 7;

        // 兑换完后剩下的瓶身的数量
        bottle = bottle % 3;
        // 兑换完后剩下的瓶身的数量
        bottleCap = bottleCap % 7;

        return all + drinkCount(newBottle, bottle, bottleCap);
    }

    public static void main(String[] args) {
        long n = 100;
        if (n >= MAX_VALUE || n < 0) {
            System.out.println("暂只0至1亿数值的计算");
        }else{
            long size = drinkCount(n, 0, 0);
            System.out.println(size);
        }
    }
}
