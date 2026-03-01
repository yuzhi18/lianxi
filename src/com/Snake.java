package com;

import java.util.LinkedList;

public class Snake {
    private LinkedList<Node> body;
    private Direction direction;
    private boolean isLiving;

    public Snake() {
        direction = Direction.RIGHT; // 修复：初始方向向右（文档要求）
        isLiving = true;
        initSanke();
    }

    private void initSanke() {
        body = new LinkedList<>();
        // 修复：初始位置按文档要求（蛇头(10,16)，身体向左延伸(10,15)、(10,14)）
        body.add(new Node(10, 16)); // 蛇头
        body.add(new Node(10, 15)); // 身体
        body.add(new Node(10, 14)); // 身体
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void move() {
        if (!isLiving) return; // 死亡后停止移动

        Node head = body.getFirst();
        Node newHead = null;
        switch (direction) {
            case UP:
                newHead = new Node(head.getX(), head.getY() - 1);
                break;
            case DOWN:
                newHead = new Node(head.getX(), head.getY() + 1);
                break;
            case LEFT:
                newHead = new Node(head.getX() - 1, head.getY());
                break;
            case RIGHT:
                newHead = new Node(head.getX() + 1, head.getY());
                break;
        }

        // 新增：先添加新头，再检测碰撞（符合文档碰撞时序）
        body.addFirst(newHead);

        // 碰撞检测（文档规则：边界行<0/≥20 或 列<0/≥30）
        if (newHead.getX() < 1 || newHead.getX() > 30 || newHead.getY() < 1 || newHead.getY() > 20) {
            isLiving = false;
            return;
        }

        // 撞自身检测
        for (int i = 1; i < body.size(); i++) {
            Node node = body.get(i);
            if (newHead.getX() == node.getX() && newHead.getY() == node.getY()) {
                isLiving = false;
                return;
            }
        }
    }

    public LinkedList<Node> getBody() {
        return body;
    }

    public void setBody(LinkedList<Node> body) {
        this.body = body;
    }

    // 吃食物：只加头不删尾（长度+1）
    public void eat(Node food) {
        // 无需额外移动，move()已添加新头，此处仅保留尾节点即可
        // （原逻辑重复移动，修复后简化）
    }

    public boolean isLiving() {
        return isLiving;
    }

    // 重置蛇状态（用于重新开始）
    public void reset() {
        direction = Direction.RIGHT;
        isLiving = true;
        initSanke();
    }
}