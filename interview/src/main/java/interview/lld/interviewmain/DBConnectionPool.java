package interview.lld.interviewmain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DBConnectionPool {

    private Queue<Connection> availableConnections = new LinkedList<>();
    private Set<Connection> usedConnections = new HashSet<>();

    private final int MAX_POOL_SIZE;
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public DBConnectionPool(int poolSize, String url, String user, String password) throws SQLException {
        this.MAX_POOL_SIZE = poolSize;
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;

        initializePool();
    }

    private void initializePool() throws SQLException {
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            availableConnections.add(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public synchronized Connection getConnection() throws SQLException {

        if (availableConnections.isEmpty()) {
            throw new RuntimeException("No available connections");
        }

        Connection conn = availableConnections.poll();
        usedConnections.add(conn);
        return conn;
    }

    public synchronized void releaseConnection(Connection conn) {

        if (conn != null) {
            usedConnections.remove(conn);
            availableConnections.add(conn);
        }
    }

    public synchronized int getAvailableConnections() {
        return availableConnections.size();
    }


    public static void main(String[] args) throws Exception {

        DBConnectionPool pool =
                new DBConnectionPool(
                        5,
                        "jdbc:mysql://localhost:3306/testdb",
                        "root",
                        "password"
                );

        Connection conn = pool.getConnection();

        // execute query

        pool.releaseConnection(conn);
    }
}