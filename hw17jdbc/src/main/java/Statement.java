public interface Statement {
    String CREATE_USER_TABLE = "create table USER (" +
            "id bigint(20) not null auto_increment" +
            "name varchar(255)" +
            "age int(3))";
}
