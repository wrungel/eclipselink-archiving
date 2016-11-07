package mfrolov.test;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipselinkLogger extends AbstractSessionLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(EclipselinkLogger.class);
    private static final String NEITHER_MESSAGE_NOR_EXCEPTION_LOGGED = "empty log entry issued by EclipseLink";

    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        int level = sessionLogEntry.getLevel();
        if (!shouldLog(level, null)) {
            // silently ignore this message
            return;
        }
        String supplementDetailString = getSupplementDetailString(sessionLogEntry);
        String message = toMessageString(sessionLogEntry);
        String logFormat = "{}{}";

        switch (level) {
            case SessionLog.OFF:
                // do not log anything
                break;
            case SessionLog.SEVERE:
                // EL is not in a state to continue
                LOGGER.error(logFormat, supplementDetailString, message);
                break;
            case SessionLog.WARNING:
                // Exceptions that don't force a stop
                LOGGER.warn(logFormat, supplementDetailString, message);
                break;
            case SessionLog.INFO:
                // Login and logout per server session with name
            case SessionLog.CONFIG:
                // Configuration info
                LOGGER.info(logFormat, supplementDetailString, message);
                break;
            case SessionLog.FINE:
                // SQL
                LOGGER.debug(logFormat, supplementDetailString, message);
                break;
            case SessionLog.FINER:
                // Previously logged under logMessage and stack trace of exceptions at WARNING level
            case SessionLog.FINEST:
                // Previously logged under logDebug
            case SessionLog.ALL:
                // Log everything
                LOGGER.trace(logFormat, supplementDetailString, message);
                break;
            default:
                // The SessionLogEntry has been constructed with a level not covered by the
                // SessionLog constants.
                // As per convention everything with a level below SessionLog.ALL is logged with
                // trace
                // and everything above SessionLog.OFF is not logged at all.
                if (level < SessionLog.ALL) {
                    LOGGER.trace(logFormat, supplementDetailString, message);
                }
                break;
        }
    }

    @Override
    public boolean shouldLog(int level, String category) {
        return isEnabledForLevel(level);
    }

    @Override
    public boolean shouldDisplayData() {
        // SQL statements are logged at SessionLog.FINE
        // If the corresponding log4j priority is activated the binding parameters shall be
        // shown
        return isEnabledForLevel(SessionLog.FINE);
    }

    @Override
    public boolean shouldPrintDate() {
        return true;
    }

    @Override
    public boolean shouldPrintSession() {
        return true;
    }

    @Override
    public boolean shouldPrintConnection() {
        return true;
    }

    @Override
    public boolean shouldPrintThread() {
        return true;
    }

    private String toMessageString(SessionLogEntry sessionLogEntry) {
        String message = formatMessage(sessionLogEntry);
        if (message == null || message.equals("")) {
            // Some warnings in EclipseLink come without message but with exception.
            // Such are supposed to already been handled by EclipseLink, though.
            if (sessionLogEntry.getException() != null) {
                return sessionLogEntry.getException().toString();
            } else {
                return NEITHER_MESSAGE_NOR_EXCEPTION_LOGGED;
            }
        }
        return message;
    }

    private boolean isEnabledForLevel(int level) {
        switch (level) {
            case SessionLog.OFF:
                return false;
            case SessionLog.SEVERE:
                return LOGGER.isErrorEnabled();
            case SessionLog.WARNING:
                return LOGGER.isWarnEnabled();
            case SessionLog.INFO:
            case SessionLog.CONFIG:
                return LOGGER.isInfoEnabled();
            case SessionLog.FINE:
                return LOGGER.isDebugEnabled();
            case SessionLog.FINER:
            case SessionLog.FINEST:
            case SessionLog.ALL:
                return LOGGER.isTraceEnabled();
            default:
                if (level < SessionLog.ALL) {
                    // finer than all
                    return LOGGER.isTraceEnabled();
                } else {
                    // less verbose than off
                    return false;
                }
        }
    }

}
