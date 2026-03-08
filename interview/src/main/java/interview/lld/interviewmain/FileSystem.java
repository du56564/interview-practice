package interview.lld.interviewmain;

import java.util.HashMap;
import java.util.Map;
/*
    ** Question - 1: Given an interface with methods:
    1. fopen(filePath) -> return int fileId
    2. fclose(fileId)
    3. read(fileId, offset)
    4. write(fileId, offset)
 */
import java.util.*;
import java.util.concurrent.locks.*;

class FileSystem {

    private Map<String, FileNode> pathMap = new HashMap<>();
    private Map<Integer, FileNode> openFiles = new HashMap<>();
    private int fileIdCounter = 1;

    class FileNode {
        StringBuilder content = new StringBuilder();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    }

    // open file
    public synchronized int fopen(String filePath) {

        FileNode file = pathMap.get(filePath);

        if (file == null) {
            file = new FileNode();
            pathMap.put(filePath, file);
        }

        int fileId = fileIdCounter++;
        openFiles.put(fileId, file);

        return fileId;
    }

    // close file
    public synchronized void fclose(int fileId) {
        openFiles.remove(fileId);
    }

    // read file
    public String read(int fileId, int offset) {

        FileNode file = openFiles.get(fileId);

        if (file == null) throw new RuntimeException("Invalid fileId");

        file.lock.readLock().lock();

        try {

            if (offset >= file.content.length()) {
                return "";
            }

            return file.content.substring(offset);

        } finally {
            file.lock.readLock().unlock();
        }
    }

    // write file
    public void write(int fileId, int offset, String data) {

        FileNode file = openFiles.get(fileId);

        if (file == null) throw new RuntimeException("Invalid fileId");

        file.lock.writeLock().lock();

        try {

            StringBuilder content = file.content;

            while (content.length() < offset) {
                content.append(' ');
            }

            for (int i = 0; i < data.length(); i++) {

                if (offset + i < content.length()) {
                    content.setCharAt(offset + i, data.charAt(i));
                } else {
                    content.append(data.charAt(i));
                }
            }

        } finally {
            file.lock.writeLock().unlock();
        }
    }

    static void main() {
        FileSystem fs = new FileSystem();

        int id = fs.fopen("file1");

        fs.write(id, 0, "hello");

        System.out.println(fs.read(id, 0));
    }
}