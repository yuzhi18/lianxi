package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends JFrame {
    private JPanel jPanel;
    private Snake snake;
    private Node food;
    private Timer timer;
    private int score; // 得分
    private int moveInterval; // 移动间隔（ms）
    private long startTime; // 游戏开始时间
    private boolean gameOver; // 游戏结束标记

    public MainFrame() {
        initFrame();
        initPanel();
        initGame(); // 初始化游戏状态
        setKeyListener();
        this.setAlwaysOnTop(true);
    }

    // 初始化/重置游戏状态
    private void initGame() {
        initSnake();
        initFood();
        score = 0;
        moveInterval = 200; // 初始间隔200ms（文档要求）
        gameOver = false;
        startTime = System.currentTimeMillis();
        initTimer();
    }

    private void setKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 游戏结束时按R重置
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        initGame();
                        jPanel.repaint();
                    }
                    return;
                }

                // 方向控制（禁止180°转向）
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (snake.getDirection() != Direction.DOWN) {
                            snake.setDirection(Direction.UP);
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (snake.getDirection() != Direction.UP) {
                            snake.setDirection(Direction.DOWN);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (snake.getDirection() != Direction.RIGHT) {
                            snake.setDirection(Direction.LEFT);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (snake.getDirection() != Direction.LEFT) {
                            snake.setDirection(Direction.RIGHT);
                        }
                        break;
                }
            }
        });
    }

    private void initTimer() {
        // 先停止旧定时器
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (gameOver) return;

                // 保存移动前蛇尾（用于吃食物时保留）
                Node tail = snake.getBody().getLast();

                // 蛇移动
                snake.move();

                // 检测是否存活
                if (!snake.isLiving()) {
                    gameOver = true;
                    timer.cancel();
                    // 计算游戏时长（秒）
                    long duration = (System.currentTimeMillis() - startTime) / 1000;
                    // 显示结束弹窗
                    JOptionPane.showMessageDialog(
                            jPanel,
                            String.format("游戏结束！\n最终得分：%d\n游戏时长：%d秒\n按R重新开始", score, duration),
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                Node head = snake.getBody().getFirst();
                // 检测是否吃到食物
                if (head.getX() == food.getX() && head.getY() == food.getY()) {
                    // 吃食物：保留尾节点（长度+1）
                    snake.getBody().addLast(tail);
                    score += 10; // 得分+10
                    // 速度提升（最低100ms）
                    moveInterval = Math.max(100, moveInterval - 10);
                    // 重新生成食物
                    try {
                        food.random(snake.getBody());
                    } catch (RuntimeException e) {
                        // 地图已满，通关
                        gameOver = true;
                        timer.cancel();
                        JOptionPane.showMessageDialog(
                                jPanel,
                                String.format("恭喜通关！\n最终得分：%d\n地图已无空位", score),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    // 重启定时器（应用新速度）
                    initTimer();
                } else {
                    // 未吃食物：移除尾节点
                    snake.getBody().removeLast();
                }

                jPanel.repaint();
            }
        };
        // 按当前间隔执行定时器
        timer.scheduleAtFixedRate(timerTask, 0, moveInterval);
    }

    private void initFood() {
        food = new Node();
        try {
            food.random(snake.getBody());
        } catch (RuntimeException e) {
            gameOver = true;
            JOptionPane.showMessageDialog(jPanel, "初始化食物失败：地图已满", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initSnake() {
        snake = new Snake();
    }

    private void initPanel() {
        jPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.clearRect(0, 0, getWidth(), getHeight());
                int gridSize = 20; // 网格大小（适配20×30地图）
                int mapWidth = 30 * gridSize; // 30列
                int mapHeight = 20 * gridSize; // 20行

                // 绘制边界墙壁（*）
                g.setColor(Color.BLACK);
                // 上下边界（第0/21行）
                g.drawRect(0, 0, mapWidth, gridSize);
                g.drawRect(0, mapHeight, mapWidth, gridSize);
                // 左右边界（第0/31列）
                g.drawRect(0, 0, gridSize, mapHeight + gridSize);
                g.drawRect(mapWidth, 0, gridSize, mapHeight + gridSize);
                // 填充边界字符
                g.drawString("*", gridSize / 2, gridSize / 2);
                g.drawString("*", mapWidth + gridSize / 2, gridSize / 2);
                g.drawString("*", gridSize / 2, mapHeight + gridSize / 2);
                g.drawString("*", mapWidth + gridSize / 2, mapHeight + gridSize / 2);

                // 绘制蛇（#）
                LinkedList<Node> body = snake.getBody();
                g.setColor(Color.GREEN);
                for (Node node : body) {
                    int x = node.getX() * gridSize;
                    int y = node.getY() * gridSize;
                    g.fillRect(x, y, gridSize - 1, gridSize - 1); // -1避免重叠
                    g.drawString("#", x + gridSize / 2, y + gridSize / 2);
                }

                // 绘制食物（红色圆形）
                g.setColor(Color.RED);
                int foodX = food.getX() * gridSize;
                int foodY = food.getY() * gridSize;
                g.fillOval(foodX, foodY, gridSize - 1, gridSize - 1);

                // 绘制得分
                g.setColor(Color.BLACK);
                g.drawString("得分：" + score, 10, mapHeight + gridSize + 20);
                g.drawString("速度：" + moveInterval + "ms/格", 100, mapHeight + gridSize + 20);
            }
        };
        jPanel.setPreferredSize(new Dimension(31 * 20, 22 * 20)); // 31列×22行（含边界）
        add(jPanel);
    }

    private void initFrame() {
        setTitle("贪吃蛇");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack(); // 自适应面板大小
        setLocationRelativeTo(null); // 窗口居中
    }

    public static void main(String[] args) {
        // Swing需在EDT线程运行
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}