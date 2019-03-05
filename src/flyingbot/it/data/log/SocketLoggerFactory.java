package flyingbot.it.data.log;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class SocketLoggerFactory {

    static ReentrantReadWriteLock lock = null;
    static Logger globalLogger = null;
	
	static {
        lock = new ReentrantReadWriteLock();
	}

	/**
     * Get socket logger attached to remote server.
     * @param Name logger name
     * @param IP remote ip
     * @param Port remote port
     * @return logger instance
	 */
	public static Logger GetInstance(String Name, String IP, int Port) {
		String name = Name == null || Name.length() < 1 ? SocketLoggerFactory.class.getCanonicalName() : Name;
		Logger tmp = Logger.getLogger(name);
		tmp.addHandler(new JSONSocketHandler(IP, Port));
		return tmp;
	}

	/**
     * Get global singleton socket logger.
	 */
	public static Logger GetSingleton(String Name, String IP, int Port) {
		boolean b = false;
        lock.readLock().lock();
        b = globalLogger == null;
        lock.readLock().unlock();
		if (b) {
            lock.writeLock().lock();
            if (globalLogger == null) {
                globalLogger = GetInstance(Name, IP, Port);
			}
            lock.writeLock().unlock();
        }
        return globalLogger;
	}
}
