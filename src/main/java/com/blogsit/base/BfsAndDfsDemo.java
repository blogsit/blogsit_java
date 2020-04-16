package com.blogsit.base;

import java.util.ArrayDeque;
import java.util.Queue;

public class BfsAndDfsDemo {

    public static  void dfs(Integer[][] M, Integer[] visit, int i) {
        for (int j = 0; j < M.length; j++) {
            if (M[i][j] == 1 && visit[j] == 0) {
                visit[j] = 1;
                dfs(M, visit, j);
                System.out.println("DFS"+ M[i][j]);
            }
        }
    }

    public static void bfs(Integer[][] M, Integer[] visit, int i) {
        ArrayDeque<Integer> q = new ArrayDeque<Integer>();
        q.add(i);
        while (q.size() > 0) {
            int temp = q.peek();
            for (int j = 0; j < M.length; j++) {
                if (M[temp][j] == 1 && visit[j] == null) {
                    visit[j] = 1;
                    q.add(j);
                    System.out.println("BFS"+ M[i][j]);
                }
            }
        }
    }

    public  static int FindCircleNum(Integer[][] M) {
        int N = M.length;
        int circle = 0; //朋友圈数
        Integer[] visit = new Integer[N];
        for (int i = 0; i < N; i++) {
            if (visit[i] == null) //还没被遍历过
            {
                //dfs(M,visit,i); //使用dfs搜索并标记与其相关的学生
                bfs(M, visit, i);   //使用bfs搜索并标记与其相关的学生
                circle++;
            }
        }
        return circle;
    }

    public static void main(String[] args) {
        Integer[][] M = { {1,2,3,4,5},  {1,2,3,4,5},  {1,2,3,4,5},  {1,2,3,4,5},  {1,2,3,4,5} };
        FindCircleNum(M);
    }
}
