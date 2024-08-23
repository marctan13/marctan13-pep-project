package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;

    //constructor
    public AccountServiceImpl(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    //Register account
    @Override
    public Account createAccount(Account account) throws SQLException{
        return accountDAO.createAccount(account);
    }

    //Login account
    @Override
    public Account getAccountByUsername(String username) throws SQLException{
        return accountDAO.getAccountByUsername(username);
    }

    //check if user exists
    @Override
    public boolean accountExists(int accountId) throws SQLException{
        return accountDAO.accountExists(accountId);
    }
}
