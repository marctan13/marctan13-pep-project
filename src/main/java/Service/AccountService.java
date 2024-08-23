package Service;

import java.sql.SQLException;
import Model.Account;

public interface AccountService {
    Account createAccount(Account account) throws SQLException;
    Account getAccountByUsername(String username) throws SQLException;
    boolean accountExists(int accountId) throws SQLException;
}
