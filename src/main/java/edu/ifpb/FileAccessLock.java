package edu.ifpb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileAccessLock {
    private int file = 0;
    private int ReadersInCriticalRegion = 0;
    private boolean isWriting = false;
    private static final Logger LOGGER = LogManager.getLogger(FileAccessLock.class);

    /**
     * Realiza a operação de leitura no recurso compartilhado.
     * A thread de leitura aguarda enquanto houver uma thread de escrita acessando o recurso.
     * Após a leitura, o número de leitores ativos é atualizado e, se não houver mais leitores,
     * notifica as threads de escrita.
     */
    public void read() {
        synchronized (this) {
            /*
             O synchronized garante que esse trecho será executado por uma thread por vez.
             Isso assegura que o incremento do contador ReadersInCriticalRegion seja feito de forma atômica, evitando condições de corrida.
            */
            LOGGER.info(Thread.currentThread().getName() + " Está na região crítica!");

            // Incrementa o número de threads de leitura na região crítica
            ReadersInCriticalRegion++;
        }

        while (isWriting) {
            LOGGER.warn("Aguardando Escrita");
        }

        // Simula o processo de leitura
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info(Thread.currentThread().getName() + " leu o valor: " + file);

        synchronized (this) {
            ReadersInCriticalRegion--;
            if (ReadersInCriticalRegion == 0) {
                LOGGER.info("Não há leitores na região crítica");
                notifyAll();  // Se não houver mais threads de leitura, notifica possíveis threads de escrita
            }
        }
    }

    /**
     * Realiza a operação de escrita no recurso compartilhado.
     * A thread de escrita aguarda enquanto houver leitores ou outra thread de escrita acessando o recurso.
     * Após a escrita, o recurso é liberado e todas as threads são notificadas.
     */
    public void write(int valueToWrite) {
        synchronized (this) {
            while (isWriting || ReadersInCriticalRegion > 0) {
                LOGGER.warn(Thread.currentThread().getName() + " - Aguardando leitores/escritores deixarem a região crítica!");
                try {
                    wait();  // Aguarda se houver threads de leitura ou outra thread de escrita
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            LOGGER.info(Thread.currentThread().getName() + " Está na região crítica!");

            // Define que uma thread de escrita está escrevendo
            isWriting = true;
        }

        // Simulaçãp do processo de escrita
        file = valueToWrite;
        LOGGER.info(Thread.currentThread().getName() + " escreveu o valor: " + file);

        synchronized (this) {
            isWriting = false;

            // Notifica todas as threads (de leitura e escrita) que o recurso foi liberado
            notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FileAccessLock fileAccessLock = new FileAccessLock();

        Thread t1 = new Thread(fileAccessLock::read, "Leitura-1");
        Thread t4 = new Thread(() -> fileAccessLock.write(42), "Escrita-1");
        Thread t2 = new Thread(fileAccessLock::read, "Leitura-2");
        Thread t5 = new Thread(() -> fileAccessLock.write(42), "Escrita-2");
        Thread t3 = new Thread(fileAccessLock::read, "Leitura-3");


        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}