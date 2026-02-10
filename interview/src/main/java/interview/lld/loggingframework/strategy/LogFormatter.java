package interview.lld.loggingframework.strategy;

import interview.lld.loggingframework.entities.LogMessage;

public interface LogFormatter {
    String format(LogMessage logMessage);
}
