package configuration;

public class InitializeStatement {
    public final static String CREATE_USER_TABLE = "create table if not exists USER (" +
            "id bigint(20) not null auto_increment," +
            "address_id bigint(20)," +
            "name varchar(255)," +
            "age int(3))";
    public final static String CREATE_ADDRESS_TABLE = "create table if not exists ADDRESS (" +
            "id bigint(20) not null auto_increment," +
            "user_id bigint(20)," +
            "street varchar(255))";
    public final static String CREATE_PHONE_TABLE = "create table if not exists PHONE (" +
            "id bigint(20) not null auto_increment," +
            "number varchar(255))";
}
