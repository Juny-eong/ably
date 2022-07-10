package com.ably.assignment.global.config.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.util.Locale;

public class CustomP6spySqlFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId,
                                String now,
                                long elapsed,
                                String category,
                                String prepared,
                                String sql,
                                String url) {
        sql = formatSql(category, sql);
        return now + " | " + elapsed + "ms | " + category + " | connection " + connectionId + " | " + sql;
    }

    private String formatSql(String category, String sql) {
        if (  sql == null || "".equals(sql.trim())  ) {
            return sql;
        }

        // Only format Statement, distinguish DDL And DML
        if (  Category.STATEMENT.getName().equals(category)  ) {
            String tempSql = sql.trim().toLowerCase(Locale.ROOT);
            if (  tempSql.startsWith("create") || tempSql.startsWith("alter") || tempSql.startsWith("comment")  ) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            }
            else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
        }
        return sql;
    }
}