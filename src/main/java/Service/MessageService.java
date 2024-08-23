package Service;

import java.sql.SQLException;
import Model.Message;
import java.util.List;

public interface MessageService {
    Message createMessage(Message message) throws SQLException;
    List<Message> getAllMessages() throws SQLException;
    Message getMessageById(int messageId) throws SQLException;
    Message deleteMessageById(int messageId) throws SQLException;
    Message updateMessageById(int messageId, String newMessage) throws SQLException;
    List<Message> getMessagesByAccountId(int accountId) throws SQLException;
}
