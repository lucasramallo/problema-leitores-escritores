package edu.ifpb;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // Recurso compartilhado
    private int contador = 0;

    // Método de leitura (pode ser acessado por múltiplas threads simultaneamente)
    public void ler() {
        lock.readLock().lock();  // Adquire o lock de leitura
        try {
            System.out.println(Thread.currentThread().getName() + " leu o valor: " + contador);
        } finally {
            lock.readLock().unlock();  // Libera o lock de leitura
        }
    }

    // Método de escrita (somente uma thread pode acessar por vez)
    public void escrever(int valor) {
        lock.writeLock().lock();  // Adquire o lock de escrita
        try {
            contador = valor;
            System.out.println(Thread.currentThread().getName() + " escreveu o valor: " + contador);
        } finally {
            lock.writeLock().unlock();  // Libera o lock de escrita
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock exemplo = new ReadWriteLock();

        // Threads de leitura
        Thread t1 = new Thread(exemplo::ler, "Leitura-1");
        Thread t4 = new Thread(() -> exemplo.escrever(42), "Escrita");
        Thread t2 = new Thread(exemplo::ler, "Leitura-2");
        Thread t3 = new Thread(exemplo::ler, "Leitura-3");

        // Thread de escrita

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }
}
