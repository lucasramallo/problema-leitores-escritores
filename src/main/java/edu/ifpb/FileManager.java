package edu.ifpb;

import java.io.*;

public class FileManager {
    private final File file;

    public FileManager() {
        this.file = new File("src/main/java/edu/ifpb/storage/File.txt");
    }

    /**
     * Escreve um valor em uma nova linha do arquivo.
     * @param value O valor a ser escrito no arquivo.
     */
    public synchronized void writeToFile(String value) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(value);
            writer.newLine();
            System.out.println("Valor escrito: " + value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lê todas as linhas do arquivo e retorna como uma lista de strings.
     * @return Uma lista contendo todas as linhas do arquivo.
     */
    public synchronized String readFromFile() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
