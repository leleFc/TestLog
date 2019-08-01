import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset
import grails.util.Environment
import grails.util.BuildSettings

scan("5 seconds")

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

def logPattern = "%date %-5level %logger{0} - %message%n"
def logHistory = "7"
def logFolder = BuildSettings.TARGET_DIR
Level logLevel = Level.INFO
def logEncoding = "UTF-8"

appender("STDOUT_FILE", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logFolder}/TestLog_%d{yyyy-MM-dd}.log"
        maxHistory = "${logHistory}"
    }
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName("${logEncoding}")
        pattern = "${logPattern}"
    }
    prudent = true
}

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        //pattern = "[%d{HH:mm:ss.SSS}] [%level] %logger - %msg%n"
        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

appender("FULL_STACKTRACE_FILE", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logFolder}/TestLog_stacktrace_%d{yyyy-MM-dd}.log"
        maxHistory = "${logHistory}"
    }
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName("${logEncoding}")
        pattern = "${logPattern}"
    }
    prudent = true
}

appender("FULL_STACKTRACE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{HH:mm:ss.SSS}] [%level] %logger - %msg%n"
    }
}

if (Environment.isDevelopmentMode()) {
    // se sono in sviluppo scrivo solo su console
    root(logLevel, ['STDOUT'])
    logger("StackTrace", logLevel, ['FULL_STACKTRACE'], false)
} else {
    // se sono in produzione scrivo solo su file
    root(logLevel, ['STDOUT_FILE'])
    logger("StackTrace", logLevel, ['FULL_STACKTRACE_FILE'], false)
}
