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

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        final ValidationErrors errors = new ValidationErrors();
        return errors;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
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
            for(int i=0;i<sqls.length;i++) {
                conn.prepareStatement(sqls[i]).execute();
            }
        } catch (SQLException e) {
            throw new CustomChangeException(e);
        } catch (DatabaseException e) {
            throw new CustomChangeException(e);
        }
    }

    @Override
    public void rollback(Database database) throws CustomChangeException, UnsupportedChangeException, RollbackImpossibleException {
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
            for(int i=0;i<sqls.length;i++) {
                System.out.println("Executing sql statement '" + sqls[i] + "'");
                conn.prepareStatement(sqls[i]).execute();
            }
        } catch (SQLException e) {
            throw new CustomChangeException(e);
        } catch (DatabaseException e) {
            throw new CustomChangeException(e);
        }

    }
}
