package com;

import java.util.Random;
import java.util.LinkedList;

public class Node {
    private int x;
    private int y;

    public Node(){}

    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY(){
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    // 修复：按文档规则生成1-20行、1-30列的随机食物（避开蛇身）
    public void random(LinkedList<Node> snakeBody) {
        Random r = new Random();
        boolean isOverlap;
        int retry = 0;
        do {
            isOverlap = false;
            // 文档规则：行1-20，列1-30（边界0/21行、0/31列为墙壁）
            this.x = r.nextInt(30) + 1; // 列1-30
            this.y = r.nextInt(20) + 1; // 行1-20

            // 检查是否和蛇体重叠
            for (Node node : snakeBody) {
                if (this.x == node.getX() && this.y == node.getY()) {
                    isOverlap = true;
                    retry++;
                    break;
                }
            }
            // 重试超过5次判定地图满
            if (retry > 5) {
                throw new RuntimeException("地图已满");
            }
        } while (isOverlap);
    }
}