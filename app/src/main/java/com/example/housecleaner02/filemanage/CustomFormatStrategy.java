package com.example.housecleaner02.filemanage;

import android.util.Log;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;

public class CustomFormatStrategy implements FormatStrategy {
    private static final int CHUNK_SIZE = 4000;
    private static final int MIN_STACK_OFFSET = 5;
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";

//    private final DateFormat dateFormat;
    private final LogStrategy logStrategy;
    private final String tag;

    private CustomFormatStrategy(Builder builder) {
        Utils.checkNotNull(builder);

//        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String onceOnlyTag, String message) {
        Utils.checkNotNull(message);

        String tag = formatTag(onceOnlyTag);

//        logTopBorder(priority, tag);
//        logHeaderContent(priority, tag);


        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (priority == Log.ASSERT) {
                logChunk(priority, tag, message);
            } else {
                logContent(priority, tag, message);
            }
//            logBottomBorder(priority, tag);
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            String chunk = new String(bytes, i, count);
            if (priority == Log.ASSERT) {
                logChunk(priority, tag, chunk);
            } else {
                logContent(priority, tag, chunk);
            }
        }
//        logBottomBorder(priority, tag);
    }

    private void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER);
    }

    private void logHeaderContent(int logType, String tag) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (trace.length <= MIN_STACK_OFFSET) {
            return;
        }
        String level = "";
        StringBuilder builder = new StringBuilder()
                .append(HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(' ')
                .append(Thread.currentThread().getName())
                .append(" ")
                .append("(")
                .append(trace[MIN_STACK_OFFSET].getFileName())
                .append(":")
                .append(trace[MIN_STACK_OFFSET].getLineNumber())
                .append(")");
        logChunk(logType, tag, builder.toString());

        builder = new StringBuilder()
                .append(HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(' ')
                .append(trace[MIN_STACK_OFFSET - 1].getMethodName())
                .append(" ");
        logChunk(logType, tag, builder.toString());
    }

    private void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
//            logChunk(logType, tag, HORIZONTAL_LINE + " " + line);
            logChunk(logType, tag, line);
        }
    }

    private void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER);
    }

    private void logChunk(int priority, String tag, String chunk) {
        logStrategy.log(priority, tag, chunk);
    }

    private String formatTag(String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
//        DateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

//        public Builder dateFormat(DateFormat val) {
//            dateFormat = val;
//            return this;
//        }

        public Builder logStrategy(LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public CustomFormatStrategy build() {
//            if (dateFormat == null) {
//                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.KOREA);
//            }
            if (logStrategy == null) {
                logStrategy = new LogcatLogStrategy();
            }
            return new CustomFormatStrategy(this);
        }
    }
}