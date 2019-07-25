package jdbc;

public class InitializeStatement {
    public final static String CREATE_USER_TABLE = "create table if not exists USER (" +
            "id bigint(20) not null auto_increment," +
            "name varchar(255)," +
            "age int(3))";
    public final static String CREATE_ACCOUNT_TABLE = "create table if not exists ACCOUNT (" +
            "number bigint(20) not null auto_increment," +
            "type varchar(255)," +
            "rest number)";
}
