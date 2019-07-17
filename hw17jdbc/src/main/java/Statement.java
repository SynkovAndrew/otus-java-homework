public interface Statement {
    String CREATE_USER_TABLE = "create table USER (" +
            "id bigint(20) not null auto_increment," +
            "name varchar (255)," +
            "age int(3))";
    String INSERT_INTO_USER_TABLE = "insert into USER (name, age) " +
            "values ('%s', %d)";
    String SELECT_ALL_FROM_USER = "select * from user";
}
