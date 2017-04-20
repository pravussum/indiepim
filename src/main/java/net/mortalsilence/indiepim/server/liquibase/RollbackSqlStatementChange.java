package net.mortalsilence.indiepim.server.liquibase;

import liquibase.change.custom.CustomTaskChange;
import liquibase.change.custom.CustomTaskRollback;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.*;
import liquibase.resource.ResourceAccessor;

import java.sql.SQLException;

/**
 * Works with single line statements only yet.
 */
public class RollbackSqlStatementChange implements CustomTaskChange, CustomTaskRollback {

    private String sql;

    public String getRollbackSql() {
        return rollbackSql;
    }

    public void setRollbackSql(String rollbackSql) {
        this.rollbackSql = rollbackSql;
    }

    private String rollbackSql;

    public String getConfirmationMessage() {
        return null;
    }

    public void setUp() throws SetupException {

    }

    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    public ValidationErrors validate(Database database) {
        return new ValidationErrors();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void execute(Database database) throws CustomChangeException {
        if(sql == null)
            throw new CustomChangeException("parameter 'sql' must be given.");
        System.out.println("Executing sql statement '" + sql + "'");
        final JdbcConnection conn = (JdbcConnection)database.getConnection();
        try {
            String[] sqls;
            if(sql.contains(";")) {
                sqls = sql.split(";");
            } else {
                sqls = new String[] {sql};
            }
            for (String sql1 : sqls) {
                conn.prepareStatement(sql1).execute();
            }
        } catch (SQLException e) {
            throw new CustomChangeException(e);
        } catch (DatabaseException e) {
            throw new CustomChangeException(e);
        }
    }

    public void rollback(Database database) throws CustomChangeException, RollbackImpossibleException {
        if(rollbackSql == null)
            throw new CustomChangeException("parameter 'rollbackSql' must be given.");
            final JdbcConnection conn = (JdbcConnection)database.getConnection();
        try {
            String[] sqls;
            if(rollbackSql.contains(";")) {
                sqls = rollbackSql.split(";");
            } else {
                sqls = new String[] {rollbackSql};
            }
            for (String curSql : sqls) {
                System.out.println("Executing sql statement '" + curSql + "'");
                conn.prepareStatement(curSql).execute();
            }
        } catch (SQLException e) {
            throw new CustomChangeException(e);
        } catch (DatabaseException e) {
            throw new CustomChangeException(e);
        }

    }
}
