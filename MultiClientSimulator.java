package socket;

public class MultiClientSimulator {
    public static void main(String[] args) {
        int clientCount = 5; // 模拟的客户端数量
        for (int i = 0; i < clientCount; i++) {
            new Thread(() -> {
                VectorCalculationClient.main(args);
            }).start();
        }
    }
}

