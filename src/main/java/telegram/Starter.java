package telegram;

import util.ConnectionPool.ConnectionPool;

class Starter {
    public static void main(String[] args) {
        new Bot().serve();
        System.out.println("Bot started");
    }
}