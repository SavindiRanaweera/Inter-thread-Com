package lk.ijse.dep13.interthreadcommunications.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

public class MontisoriCP {
    private static final int DEFAULT_POOL_SIZE = 5;

    private final HashMap<Integer, Connection> MAIN_POOL = new HashMap<>();
    private final HashMap<Integer, Connection> CONSUMER_POOL = new HashMap<>();
    private int poolSize ;

    public MontisoriCP() {
        this(DEFAULT_POOL_SIZE);
    }

    public MontisoriCP(int poolSize) {
        this.poolSize = poolSize;
        try {
            initializePool();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public int getPoolSize() {
        return poolSize;
    }

    private void initializePool() throws IOException, SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));

        String host = properties.getProperty("app.db.host");
        String port = properties.getProperty("app.db.port");
        String database = properties.getProperty("app.db.database");
        String user = properties.getProperty("app.db.user");
        String password = properties.getProperty("app.db.password");
        poolSize = Integer.parseInt ( properties.getProperty ( "app.db.poolSize") );

        Class.forName("com.mysql.cj.jdbc.Driver");

        for (int i = 0; i < poolSize; i++) {
            Connection connection = DriverManager.getConnection (
                    "jdbc:mysql://%s:%s/%s".formatted ( host, port, database ), user, password );

            String uuidKey = UUID.randomUUID ( ).toString ( );
            MAIN_POOL.put ( Integer.valueOf ( uuidKey ), connection );
        }
    }

    public record ConnectionWrapper(Integer id, Connection connection) {
    }

    public synchronized ConnectionWrapper getConnection() {
        while (MAIN_POOL.isEmpty()) {
            try{
                wait ();
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
        Integer key = MAIN_POOL.keySet().stream().findFirst().get();
        Connection connection = MAIN_POOL.get(key);
        MAIN_POOL.remove(key);
        CONSUMER_POOL.put(key, connection);
        return new ConnectionWrapper(key, connection);

    }

    public synchronized void releaseConnection(Integer id){
        if (!CONSUMER_POOL.containsKey(id)) throw new RuntimeException("Invalid Connection ID");
        Connection connection = CONSUMER_POOL.get(id);
        CONSUMER_POOL.remove(id);
        MAIN_POOL.put(id, connection);
        notify ();
    }

    public void releaseAllConnections(){
        CONSUMER_POOL.forEach(MAIN_POOL::put);
        CONSUMER_POOL.clear();
    }


}
