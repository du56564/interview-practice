package interview.lld.loggingframework.strategy;

import interview.lld.loggingframework.entities.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
    void close();
    LogFormatter getFormatter();
    void setFormatter(LogFormatter formatter);
}