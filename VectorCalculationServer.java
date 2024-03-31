package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

class VectorCalculationServer {
    public static void main(String[] args) {
        int portNumber = 8888; // 服务器监听的端口号

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("服务器启动，监听端口：" + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("接收到新的客户端连接");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] input = inputLine.split(" ");
                    if (input.length != 3) {
                        out.println("Invalid input");
                        continue;
                    }

                    String operation = input[0];
                    String[] vectorA = input[1].split(",");
                    String[] vectorB = input[2].split(",");

                    if (operation.equals("dot")) {
                        if (vectorA.length != vectorB.length) {
                            out.println("Invalid input lengths");
                            continue;
                        }
                        double[] vA = Arrays.stream(vectorA).mapToDouble(Double::parseDouble).toArray();
                        double[] vB = Arrays.stream(vectorB).mapToDouble(Double::parseDouble).toArray();
                        double dotProduct = dotProduct(vA, vB);
                        out.println("Dot product: " + dotProduct);
                    } else if (operation.equals("cross")) {
                        if (vectorA.length != 3 || vectorB.length != 3) {
                            out.println("Cross product requires 3-dimensional vectors");
                            continue;
                        }
                        double[] vA = Arrays.stream(vectorA).mapToDouble(Double::parseDouble).toArray();
                        double[] vB = Arrays.stream(vectorB).mapToDouble(Double::parseDouble).toArray();
                        double[] crossProduct = crossProduct(vA, vB);
                        out.println("Cross product: " + Arrays.toString(crossProduct));
                    } else {
                        out.println("Invalid operation");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private double dotProduct(double[] a, double[] b) {
            double result = 0;
            for (int i = 0; i < a.length; i++) {
                result += a[i] * b[i];
            }
            return result;
        }

        private double[] crossProduct(double[] a, double[] b) {
            return new double[]{
                    a[1] * b[2] - a[2] * b[1],
                    a[2] * b[0] - a[0] * b[2],
                    a[0] * b[1] - a[1] * b[0]
            };
        }
    }
}

