package edu.ifpb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private int counter = 0;
    private int ReadersInCriticalRegion = 0;  // counter de threads de leitura
    private boolean isWriting = false;  // Flag que indica se uma thread de escrita está acessando o recurso
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    // Método de leitura (pode ser acessado por várias threads simultaneamente)
    public void read() {
        synchronized (this) {
            LOGGER.info(Thread.currentThread().getName() + " Está na região crítica!");
            ReadersInCriticalRegion++;  // Incrementa dentro do bloco sincronizado
        }  // Incrementa dentro do bloco sincronizado

        while (isWriting) {  // Espera enquanto houver uma thread de escrita acessando
            LOGGER.warn("Aguardando Escrita");
        }

        // Simula o processo de leitura
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info(Thread.currentThread().getName() + " leu o valor: " + counter);

        synchronized (this) {
            ReadersInCriticalRegion--;  // Decrementa o counter de threads de leitura
            if (ReadersInCriticalRegion == 0) {
                LOGGER.info("Não há leitores na zona região crítica");
                notifyAll();  // Se não houver mais threads de leitura, notifica possíveis threads de escrita
            }
        }
    }

    // Método de escrita (somente uma thread pode acessar por vez)
    public void write(int valor) {
        synchronized (this) {
            while (isWriting || ReadersInCriticalRegion > 0) {  // Espera se houver threads de leitura ou outra thread de escrita
                LOGGER.warn(Thread.currentThread().getName() + " - Aguardando leitores deixarem a região crítica!");
                try {
                    wait();  // Aguarda se houver threads de leitura ou outra thread de escrita
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isWriting = true;  // Define que uma thread de escrita está acessando
        }

        // Simula o processo de escrita
        counter = valor;
        LOGGER.info(Thread.currentThread().getName() + " escreveu o valor: " + counter);

        synchronized (this) {
            isWriting = false;  // Libera o acesso à escrita
            notifyAll();  // Notifica todas as threads (de leitura e escrita) que o recurso foi liberado
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Main exemplo = new Main();

        Thread t1 = new Thread(exemplo::read, "Leitura-1");
        Thread t4 = new Thread(() -> exemplo.write(42), "Escrita-1");
        Thread t2 = new Thread(exemplo::read, "Leitura-2");
        Thread t5 = new Thread(() -> exemplo.write(42), "Escrita-2");
        Thread t3 = new Thread(exemplo::read, "Leitura-3");


        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}











//package edu.ifpb;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class Main {
//    private final ReentrantLock lock = new ReentrantLock();
//
//    public void incrementar() {
//        String threadName = Thread.currentThread().getName();
//
//        System.out.println(threadName + " Solicitou acesso em: " + System.nanoTime());
//
//        // Se a thread for "Thread-A", ela pode acessar sem aguardar o lock
//        if (threadName.equals("Thread-A")) {
//            System.out.println(threadName + " Acessou sem esperar pelo lock");
//            try {
//                Thread.sleep(4000); // Simulando a operação
//                System.out.println(threadName + " Saiu");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            lock.lock(); // Bloqueia as outras threads normalmente
//            try {
//                Thread.sleep(4000);
//                System.out.println(threadName + " Acessou");
//                System.out.println("Em threads em lista de espera: " + lock.getQueueLength());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                System.out.println(threadName + " Saiu");
//                lock.unlock(); // Libera o lock
//            }
//        }
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        Main exemplo = new Main();
//
//        // Cria três threads, com a "Thread-A" sendo a que pode acessar sem bloquear
//        Thread t1 = new Thread(exemplo::incrementar, "Thread-A");
//        Thread t2 = new Thread(exemplo::incrementar, "Thread-B");
//        Thread t3 = new Thread(exemplo::incrementar, "Thread-B");
//
//        t1.start();
//        t2.start();
//        t3.start();
//    }
//}
