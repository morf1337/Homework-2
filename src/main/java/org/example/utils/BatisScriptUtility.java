package org.example.utils;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Objects;

public class BatisScriptUtility {
    public static void runInitScript() throws SQLException {
        ScriptRunner scriptRunner = new ScriptRunner(DatabaseConnection.getConnection());
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.runScript(
                new InputStreamReader(Objects.requireNonNull(
                        BatisScriptUtility.class.getClassLoader().getResourceAsStream("data.sql"))));
    }
}
