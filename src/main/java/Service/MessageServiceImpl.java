package Service;

import java.sql.SQLException;

import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import java.util.List;

public class MessageServiceImpl implements MessageService{
    private final MessageDAO messageDAO;
    private final AccountService accountService;

    //constructor
    public MessageServiceImpl(MessageDAO messageDAO, AccountService accountService){
        this.messageDAO = messageDAO;
        this.accountService = accountService;
    }

//Create new message
@Override
public Message createMessage(Message message) throws SQLException{
    //validate user
    if(!accountService.accountExists(message.getPosted_by())){
        throw new SQLException("Invalid User");
    }

    //validate message text
    if(message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255){
        throw new SQLException("Invalid message text");
    }

    return messageDAO.createMessage(message);
}

//Get all messages
@Override
public List<Message> getAllMessages() throws SQLException{
    return messageDAO.getAllMessages();
}

//Get message by message id
@Override
public Message getMessageById(int messageId) throws SQLException{
    return messageDAO.getMessageById(messageId);
}

//Delete message by message id
@Override
public Message deleteMessageById(int messageId) throws SQLException{
    return messageDAO.deleteMessageById(messageId);
}

//Update message by message id
@Override
public Message updateMessageById(int messageId, String newMessage) throws SQLException{
    //validate message text
    if(newMessage == null || newMessage.isBlank() || newMessage.length() > 255){
        throw new SQLException("Invalid message text");
    }

    //check for existing message
    Message existingMessage = messageDAO.getMessageById(messageId);
    if(existingMessage == null){
        throw new SQLException("Message not found");
    }

    //update the message text
    existingMessage.setMessage_text(newMessage);

    return messageDAO.updateMessage(existingMessage);
}

//Get all messages by account Id
@Override
public List<Message> getMessagesByAccountId(int accountId) throws SQLException{
    return messageDAO.getMessagesByAccountId(accountId);
}
}
