package DAO;

import java.sql.*;

import Model.Message;
import Util.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    
    //create new message
    public Message createMessage(Message message) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        //Validates message is not blank and under 255 characters
        if(message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255){
            throw new SQLException("Invalid Message");
        }

        //Validate posted by refers to an existing User

        String checkUserSQL = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        PreparedStatement psUser = connection.prepareStatement(checkUserSQL);
        psUser.setInt(1, message.getPosted_by());
        ResultSet userResults = psUser.executeQuery();
        //If count is zero
        if(userResults.next() && userResults.getInt(1) == 0){
            throw new SQLException("Invalid User");
        }

            
            //Set up query
            String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?,?,?)";
            //Prepare Statement
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //set values on placeholders
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();

            //Set generated id
            ResultSet results = ps.getGeneratedKeys();
            if(results.next()){
                //get id
                int generatedId = results.getInt(1);
                //set generated id using the object's setter method
                message.setMessage_id(generatedId);
            }
            return message;
    }

    //get all messages
    public List<Message> getAllMessages() throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message";
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(sql);

        List<Message> messages = new ArrayList<>();
        while(results.next()){
            //create object
            Message message = new Message(
                results.getInt("message_id"),
                results.getInt("posted_by"),
                results.getString("message_text"),
                results.getLong("time_posted_epoch")
            );
            //add to list
            messages.add(message);
        }
        return messages;
    }

    //get message given by message id
    public Message getMessageById(int messageId) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, messageId);

        ResultSet results = ps.executeQuery();
        //use if condition instead of while since its expected to get 1 row(1 message); 
        if(results.next()){
            return new Message(
                results.getInt("message_id"),
                results.getInt("posted_by"),
                results.getString("message_text"),
                results.getLong("time_posted_epoch")
            );
        }
        return null;
    }

    //Delete message by message Id
    public Message deleteMessageById(int messageId) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();

        //Fetch message to be deleted
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, messageId);

        ResultSet result = ps.executeQuery();

        if(result.next()){
            //create object if message exists
            Message message = new Message(
                result.getInt("message_id"),
                result.getInt("posted_by"),
                result.getString("message_text"),
                result.getLong("time_posted_epoch")
            );
            //Delete the message
            String deleteSQL = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement deletePs = connection.prepareStatement(deleteSQL);
            deletePs.setInt(1, messageId);
            deletePs.executeUpdate();
    
            return message;
        } else{
            //if no message exists
            return null;
        }
    }

    //Update message given message id
    public Message updateMessage(Message message) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, message.getMessage_text());
        ps.setInt(2, message.getMessage_id());

        int rowsUpdated = ps.executeUpdate();
        if(rowsUpdated > 0){
            String selectSQL = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement selectPs = connection.prepareStatement(selectSQL);
            selectPs.setInt(1, message.getMessage_id());
            ResultSet result = selectPs.executeQuery();
            if(result.next()){
                return new Message(
                    result.getInt("message_id"),
                    result.getInt("posted_by"),
                    result.getString("message_text"),
                    result.getLong("time_posted_epoch")
                );
            }
        }
        return null;
    }

    //Get messages by account Id
    public List<Message> getMessagesByAccountId(int accountId) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, accountId);

        ResultSet results = ps.executeQuery();
        List<Message>messages = new ArrayList<>();
        while(results.next()){
            Message message = new Message(
                results.getInt("message_id"),
                results.getInt("posted_by"),
                results.getString("message_text"),
                results.getLong("time_posted_epoch")
            );
            messages.add(message);
        }
        return messages;
    }
}
