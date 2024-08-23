package DAO;

import java.sql.*;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    //Register account
    public Account createAccount(Account account) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        //Validate input is not empty
        if(account.getUsername() == null || account.getUsername().isBlank()){
            throw new SQLException("Username cannot be null or blank");
        }

        //Validate password is at least 4 characters long
        if(account.getPassword() == null && account.getPassword().length() < 4){
            throw new SQLException("Password should be at least 4 characters");
        }


        //validate if username already exist
        String checkUserSQL = "SELECT COUNT(*) FROM account WHERE username = ?";
        PreparedStatement userStatementCheck = connection.prepareStatement(checkUserSQL);
        userStatementCheck.setString(1, account.getUsername());
        
        ResultSet result = userStatementCheck.executeQuery();

        //Counts more than 1 row with given username
        if(result.next() && result.getInt(1) > 0){
            throw new SQLException("Username already exists");
        }


        //Define sql query to insert inputs to table
        String sql = "INSERT INTO account(username, password) VALUES(?,?)";
        //Prepare Statement
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        //State placeholder values
        ps.setString(1, account.getUsername());
        ps.setString(2, account.getPassword());

        ps.executeUpdate();

        //set generated account_id
        ResultSet generatedKey = ps.getGeneratedKeys();
        if(generatedKey.next()){
            int generateId = generatedKey.getInt(1);
            //set id value using object's setter method
            account.setAccount_id(generateId);
        } 
        return account;
    }

    //Get account by user name(log in)
    public Account getAccountByUsername(String username) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();

        String sql = "SELECT * FROM account WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, username);

        ResultSet result = ps.executeQuery();
        if(result.next()){
            return new Account(
                result.getInt("account_id"),
                result.getString("username"),
                result.getString("password")
            );
        }
        return null;
    }

    //Get account by Id to see if user exists
    public boolean accountExists(int accountId) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, accountId);

        ResultSet result = ps.executeQuery();
        if(result.next()){
            //checks count
            return result.getInt(1) > 0;
        }
        return false;
    }

}
