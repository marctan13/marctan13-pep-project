package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import DAO.AccountDAO;
import DAO.MessageDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {

    private final AccountDAO accountDAO = new AccountDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // endpoint to create a new account
        app.post("/register", this::registerAccount);

        // endpoint to log in an account
        app.post("/login", this::loginAccount);

        // endpoint to create new message
        app.post("/messages", this::createMessage);

        // endpoint to get all messages
        app.get("/messages", this::getAllMessages);

        // endpoint to get message by Id
        app.get("/messages/{messageId}", this::getMessageById);

        // endpoint to delete message by Id
        app.delete("/messages/{messageId}", this::deleteMessageById);

        // endpoint to update message by Id
        app.patch("/messages/{messageId}", this::updateMessageById);

        // endpoint to get messages by Account Id
        app.get("/accounts/{accountId}/messages", this::getMessagesByAccountId);

        return app;
    }

        /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    // register account
    private void registerAccount(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);

            // if username is blank
            if (account.getUsername() == null || account.getUsername().isBlank()) {
                context.status(400).json("");
                return;
            }

            // if password is less than 4 characters
            if (account.getPassword() == null || account.getPassword().length() < 4) {
                context.status(400).json("");
                return;
            }

            // check if username exists in database
            if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
                context.status(400).json("");
                return;
            }

            Account createdAccount = accountDAO.createAccount(account);
            context.status(200).json(createdAccount);
        } catch (SQLException e) {
            context.status(400).json("Error creating account");
        }
    }

    //log in account
    private void loginAccount(Context context){
        try{
            Account account = context.bodyAsClass(Account.class);
            Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());

            //checks if password matches and there is an existing account
            if(existingAccount != null && existingAccount.getPassword().equals(account.getPassword())){
                context.status(200).json(existingAccount);
            } else{
                context.status(401).json("");
            }
        }catch(SQLException e){
            context.status(401).json("Error during login");
        }
    }

    //create message
    private void createMessage(Context context){
        try{
            Message message = context.bodyAsClass(Message.class);

            //validate message text meets requirements
            if(message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255){
                context.status(400).json("");
                return;
            }

            //validate user exists
            if(!accountDAO.accountExists(message.getPosted_by())){
                context.status(400).json("");
                return;
            }

            Message createdMessage = messageDAO.createMessage(message);
            context.status(200).json(createdMessage);

        }catch(SQLException e){
            context.status(400).json("Error creating message");
        }
    }

    //get all messages
    public void getAllMessages(Context context){
        try {
            List<Message> messages = messageDAO.getAllMessages();
            context.status(200).json(messages);
        } catch (SQLException e) {
            context.status(400).json("Error retrieving messages");
        }
    }

    //get message by message id
    private void getMessageById(Context context){
        try {
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Message message = messageDAO.getMessageById(messageId);

            if(message != null){
                context.status(200).json(message);
            } else{
                context.status(200).json("");
            }
        } catch (SQLException e) {
            context.status(400).json("Error retrieving message by Id");
        }
    }

    //Delete message by message id
    private void deleteMessageById(Context context){
        try{
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Message deletedMessage = messageDAO.deleteMessageById(messageId);

            if(deletedMessage != null){
                context.status(200).json(deletedMessage);
            } else{
                context.status(200).json("");
            }

        }catch(SQLException e){
            context.status(400).json("Failed to delete message");
        }
    }

    //Update message by message id
    private void updateMessageById(Context context){
        try{
            int messageId = Integer.parseInt(context.pathParam("messageId"));
            Message updatedMessage = context.bodyAsClass(Message.class);

            
            //verify message text
            if(updatedMessage.getMessage_text() == null || updatedMessage.getMessage_text().isBlank() || updatedMessage.getMessage_text().length() > 255){
                context.status(400).json("");
                return;
            }
            
            //set message id
            updatedMessage.setMessage_id(messageId);

            //update the message
            Message result = messageDAO.updateMessage(updatedMessage);

            if(result != null){
                context.status(200).json(result);
            } else{
                context.status(400).json("");
            }
        }catch(SQLException e){
            context.status(400).json("Error updating message");
        }
    }

    //get messages by account id
    private void getMessagesByAccountId(Context context){
        try{
            int accountId = Integer.parseInt(context.pathParam("accountId"));

            List<Message> messages = messageDAO.getMessagesByAccountId(accountId);
            context.status(200).json(messages);
        }catch(SQLException e){
            context.status(400).json("Error retrieving messages");
        }
    }



}