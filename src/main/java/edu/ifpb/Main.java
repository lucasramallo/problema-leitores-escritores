package edu.ifpb;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class Main {
    private static final Semaphore semaforo = new Semaphore(1, true);

    public static void main(String[] args) throws InterruptedException {
        Runnable tarefa = () -> {
            System.out.println(Thread.currentThread().getName() + " - " + LocalTime.now());
            try {
                System.out.println(Thread.currentThread().getName() + " tentando acessar o recurso...");
                semaforo.acquire(); // Solicita uma permissão
                System.out.println(Thread.currentThread().getName() + " obteve acesso ao recurso.");
                Thread.sleep(2000); // Simula uso do recurso
                System.out.println(Thread.currentThread().getName() + " liberando o recurso.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaforo.release(); // Libera a permissão
            }
        };

        // Cria e inicia 3 threads
        Thread t1 = new Thread(tarefa, "Thread-1");
        Thread t2 = new Thread(tarefa, "Thread-2");
        Thread t3 = new Thread(tarefa, "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }
}











//package edu.ifpb;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.concurrent.CountDownLatch;
//
//public class Main {
//    private final CountDownLatch startLatch = new CountDownLatch(1); // Para liberar todas as threads ao mesmo tempo
//    private final CountDownLatch doneLatch = new CountDownLatch(2);  // Para esperar ambas as threads terminarem
//
//    public void metodoTestado() {
//        try {
//            // Aguarda a liberação para começar
//            startLatch.await();
//            System.out.println(Thread.currentThread().getName() + " executando o método.");
//            System.out.println(LocalTime.now());
//            // Simula algum processamento
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } finally {
//            doneLatch.countDown(); // Indica que a thread terminou
//        }
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        Main teste = new Main();
//
//        // Cria duas threads que chamarão o método
//        Thread t1 = new Thread(teste::metodoTestado, "Thread-1");
//        Thread t2 = new Thread(teste::metodoTestado, "Thread-2");
//
//        t1.start();
//        t2.start();
//
//        // Libera todas as threads para começar
//        System.out.println("Liberando threads...");
//        teste.startLatch.countDown();
//
//        // Aguarda todas as threads terminarem
//        teste.doneLatch.await();
//
//        System.out.println("Todas as threads concluíram.");
//    }
//}
