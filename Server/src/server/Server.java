
package server;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    
    static ResultSet resultSet;
    ServerSocket serverSocket;
    Socket socket;
    Statement stmts ;
    static Connection con;
     Connection conn = null;
     ResultSet rs = null;
     

public Server() 
{                
    try {
            
            DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
            con = getConnection();

             serverSocket = new ServerSocket(7001);
             while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
             }
           
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally { 
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}



public static Connection getConnection() {
        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/GiftGenie","gift","genie");
            
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
}



public class ClientHandler implements Runnable {
    
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            String request = input.readLine();
            if (request != null) {
            String[] reqArr = request.split(":");
            switch (reqArr[0]) {
            case "login":
                String loginUsername = reqArr[1];
                String loginPassword = reqArr[2];
                boolean authenticated = loginFunction(loginUsername, loginPassword);
                output.println(authenticated);
                break;
            case "signup":
                String firstName = reqArr[1];
                String lastName = reqArr[2];
                String newUsername = reqArr[3];
                String phone = reqArr[4];
                String email = reqArr[5];
                String birthDate = reqArr[6];
                String newPassword = reqArr[7];
                String secretAnswer = reqArr[8];
                boolean registerCheck = signupFunction(firstName, lastName, newUsername, phone, email, birthDate, newPassword , secretAnswer);
                output.println(registerCheck);
                break;
            case "editPass":
                String usernameEdit = reqArr[1];
                String answerCheck = reqArr[2];
                String passwordChange = reqArr[3];
                boolean editCheck = editPassFunction(usernameEdit, answerCheck, passwordChange);
                output.println(editCheck);
                break;
            case "editPhone":
                String usernamePhone = reqArr[1];
                String newPhone = reqArr[2];
                String editPassCheck = reqArr[3];
                boolean editPhone = editPhoneFunction(usernamePhone , newPhone, editPassCheck);
                output.println(editPhone);
                break;
            case "money":
                String usernameCash = reqArr[1];
                String cashStream = calcMoney( usernameCash);
                output.println(cashStream);
                break;
            case "redeem":
                String usernameRedeem = reqArr[1];
                String currentCash = reqArr[2];
                boolean redeemCheck = redeemMoney(usernameRedeem,currentCash);
                output.println(redeemCheck);
                break;
            case "friend":
                String Username = reqArr[1];
                String usn = showFriendList(Username);
                output.println(usn);
                break;
            case "deleteFriend":
                 String owner_Username = reqArr[1];
                 String username_friend = reqArr[2];
                 boolean numOfRowsDel = deleteFriend(owner_Username,username_friend);
                 output.println(numOfRowsDel);
                break;
            case "addFriend":    
                String friendName = reqArr[1];
                String userName   = reqArr[2];
                String dataOfNewFriend = newFriendData(friendName,userName) ;
                output.println(dataOfNewFriend);
                break;
            case "FriendRequest":
                String User_name = reqArr[1];
                String requiredFriend =reqArr[2];
                String status   = reqArr[3];
                boolean  sendFriendRequest = sendFriendRequest(User_name,requiredFriend ,status);
                output.println(sendFriendRequest);
                break; 
            case "wishlist":
                String wishlistUsername = reqArr[1];
                String wish = showWishList(wishlistUsername);
                output.println(wish);
                break; 
            case "additem":
                String userNameItem = reqArr[1];
                String idItem = reqArr[2];
                String priceItem = reqArr[3];
                String checkItem = addItem(userNameItem,idItem,priceItem);
                output.println(checkItem);
                break; 
            case "contribute":
                String contUsername = reqArr[1];
                String friendUsername = reqArr[2];
                String contribution = reqArr[3];
                String itemName = reqArr[4];
                String checkCont = contributeFunction(contUsername,friendUsername,contribution,itemName);
                output.println(checkCont);
                break;
            case "claim":
                 String claimUsername = reqArr[1];
                 String claimItemName = reqArr[2];
                 String checkClaim = claimFunction (claimUsername,claimItemName);
                 output.println(checkClaim);
                 break;
            case "notify":
                 String notifyUsername = reqArr[1];
                 String notification = getNotifications (notifyUsername);
                 output.println(notification);
                break;
            case "showMyFriendRequest":
                String user_name   = reqArr[1];
                String allFriendRequests = selectFriendRequests(user_name);
                output.println(allFriendRequests);
                break;
            case "acceptFriend":
                String username_friend_Second_Col = reqArr[1];
                String personWhoSendRequest = reqArr[2]; 
                String Status = reqArr[3];  
                boolean ensure =  insertNewFriend( username_friend_Second_Col , personWhoSendRequest , Status);
                output.println(ensure);
                break;
            case "cancelFriendRequest":
                String Username_Friend_Second_Col = reqArr[1];  
                String PersonWHOsendRequest =reqArr[2]; 
                String StatuS = reqArr[3]; 
                boolean ensureRem =  removeRowFromFriendRequest ( Username_Friend_Second_Col , PersonWHOsendRequest , StatuS);
                output.println(ensureRem); 
                break;
            default:
                break;

            }

            clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
public String selectFriendRequests(String username) throws SQLException {

    String sel = "SELECT username_owner , status FROM friendrequest WHERE username_friend = ?";

    String total = "";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(sel);
        stmt.setString(1, username);
        rs = stmt.executeQuery();
        while (rs.next()) {
            String firendname = rs.getString("username_owner");
            String status = rs.getString("status");
            total = total + firendname + ":" + status + "#";
        }
        if (total.isEmpty()) {
            total = "no friend request";
        }
        return total;
    } catch (SQLException e) {
        return "fail";

    }
 } 
public String contributeFunction(String contUsername, String friendUsername, String contribution, String itemName) throws SQLException {
    String checkCashQuery = "SELECT CASH FROM USERLOGINS WHERE USERNAME=?";
    String getCashQuery = "UPDATE USERLOGINS SET CASH=CASH-? WHERE USERNAME=?";
    String getItemIdQuery = "SELECT ID_OF_ITEM, REMAINPRICE FROM WISHLIST_ITEM WHERE username_owner=? AND ID_OF_ITEM=(SELECT ID_OF_ITEM FROM ITEMS WHERE ITEM_NAME=?)";
    String updateRemainPriceQuery = "UPDATE WISHLIST_ITEM SET REMAINPRICE=REMAINPRICE-? WHERE ID_OF_ITEM=? AND username_owner=?";
    String insertContributionQuery = "INSERT INTO CONTRIBUTION (USERNAME_Contributed, USERNAME_OWNER, ID_OF_ITEM, AMOUNT_OF_CONTRIBUTION) VALUES (?, ?, ?, ?)";
    String insertGiftNotificationQuery = "INSERT INTO NotificationHandling (USERNAME_OWNER, DESCRIPTION) VALUES (?, ?)";

    PreparedStatement checkCashStmt = null;
    PreparedStatement getCashStmt = null;
    PreparedStatement getItemIdStmt = null;
    PreparedStatement updateRemainPriceStmt = null;
    PreparedStatement insertContributionStmt = null;
    ResultSet rs = null;
    try {
        checkCashStmt = getConnection().prepareStatement(checkCashQuery);
        checkCashStmt.setString(1, contUsername);
        rs = checkCashStmt.executeQuery();
        if (rs.next()) {
            int cash = rs.getInt("CASH");
            if (cash < Integer.parseInt(contribution)) {
                return "lowMoney";
            }
        }

        getItemIdStmt = getConnection().prepareStatement(getItemIdQuery);
        getItemIdStmt.setString(1, friendUsername);
        getItemIdStmt.setString(2, itemName);
        rs = getItemIdStmt.executeQuery();
        if (rs.next()) {
            String itemId = rs.getString("ID_OF_ITEM");
            int remainPrice = rs.getInt("REMAINPRICE");

            int contributedAmount = Integer.parseInt(contribution);
            if (contributedAmount >= remainPrice) {
                contributedAmount = remainPrice;
            }
            updateRemainPriceStmt = getConnection().prepareStatement(updateRemainPriceQuery);
            updateRemainPriceStmt.setInt(1, contributedAmount);
            updateRemainPriceStmt.setString(2, itemId);
            updateRemainPriceStmt.setString(3, friendUsername);
            updateRemainPriceStmt.executeUpdate();
            
            getCashStmt = getConnection().prepareStatement(getCashQuery);
            getCashStmt.setInt(1, contributedAmount);
            getCashStmt.setString(2, contUsername);
            getCashStmt.executeUpdate();
            insertContributionStmt = getConnection().prepareStatement(insertContributionQuery);
            insertContributionStmt.setString(1, contUsername);
            insertContributionStmt.setString(2, friendUsername);
            insertContributionStmt.setString(3, itemId);
            insertContributionStmt.setInt(4, contributedAmount);
            insertContributionStmt.executeUpdate();
            String Description;
            if (remainPrice - contributedAmount == 0) {
                Description =  itemName + " Gift is completed!";
            } else {
                Description = contUsername + " contributed in " + itemName + " by " + contributedAmount;
            }
            String insertNotificationQuery = "INSERT INTO NotificationHandling (USERNAME_OWNER, DESCRIPTION) VALUES (?, ?)";
            PreparedStatement insertNotificationStmt = null;
            try {
            insertNotificationStmt = getConnection().prepareStatement(insertNotificationQuery);
            insertNotificationStmt.setString(1, friendUsername);
            insertNotificationStmt.setString(2, Description);
            insertNotificationStmt.executeUpdate();
            } catch (SQLException e) {
            e.printStackTrace();
            throw e;
            } finally {
            if (insertNotificationStmt != null) {
            insertNotificationStmt.close();
            }
            }
            return String.valueOf(contributedAmount);
            } else {
                return "fail";
            }
	
    } catch (SQLException e) {
        return "fail";
        
    } finally {
        if (rs != null) {
            rs.close();
        }
        if (checkCashStmt != null) {
            checkCashStmt.close();
        }
        if (getCashStmt != null) {
            getCashStmt.close();
        }
        if (getItemIdStmt != null) {
            getItemIdStmt.close();
        }
       
    }
}
public String showWishList(String username) throws SQLException {
    String query = "SELECT i.ITEM_NAME, i.ITEM_DESCRIPTION, i.GROSS_PRICE, w.REMAINPRICE, w.ID_OF_ITEM\n"
            + "FROM WISHLIST_ITEM w\n"
            + "JOIN ITEMS i ON w.ID_OF_ITEM = i.ID_OF_ITEM\n"
            + "WHERE w.username_owner = ?";

    String total = "";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        rs = stmt.executeQuery();
        boolean isEmpty = true; 
        while (rs.next()) {
            isEmpty = false; 
            String itemName = rs.getString("ITEM_NAME");
            String itemDesc = rs.getString("ITEM_DESCRIPTION");
            String grossPrice = rs.getString("GROSS_PRICE");
            String remainPrice = rs.getString("REMAINPRICE");
            String itemId = rs.getString("ID_OF_ITEM");

            total = total + itemName + ":" + itemDesc + ":" + grossPrice + ":" + remainPrice + ":" + itemId + "#";
        }
        if (!isEmpty) { 
            return total;
        } else {
            return "fail";
        }
    } catch (SQLException e) {
        return "fail";
        
    }
}
public String claimFunction(String username, String itemName) throws SQLException {
    String query = "SELECT i.ID_OF_ITEM\n"
            + "FROM ITEMS i\n"
            + "JOIN WISHLIST_ITEM w ON w.ID_OF_ITEM = i.ID_OF_ITEM\n"
            + "WHERE i.ITEM_NAME = ? AND W.USERNAME_OWNER = ? AND w.REMAINPRICE = 0";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, itemName);
		stmt.setString(2, username);
        rs = stmt.executeQuery();
        boolean isEmpty = true; 
        while (rs.next()) {
            isEmpty = false;
            int itemId = rs.getInt("ID_OF_ITEM");
            String deleteQuery = "DELETE FROM WISHLIST_ITEM WHERE ID_OF_ITEM = ? AND USERNAME_OWNER = ?";
            PreparedStatement deleteStmt = getConnection().prepareStatement(deleteQuery);
            deleteStmt.setInt(1, itemId);
            deleteStmt.setString(2, username);
            int deletedRows = deleteStmt.executeUpdate();

            if (deletedRows > 0) {
                return "success";
            } else {
                return "fail";
            }
        }
        if (isEmpty) { 
            return "fail";
        }
    } catch (SQLException e) {
       return "fail";
    }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
    return "fail"; 
}
public boolean insertNewFriend(String username_friend_Second_Col, String personWhoSendRequest, String status) throws SQLException {

    String del = "delete from friendrequest where username_owner = ? and username_friend = ? and status =? ";
    String insertQuery = "INSERT INTO notificationhandling (username_owner, description) VALUES (?, ?)";
    PreparedStatement stmt_1 = null;
    PreparedStatement stmt_2 = null;
    ResultSet rs = null;

    int rowAffected = 0;

    try {
        stmt_1 = getConnection().prepareStatement(del);
        stmt_1.setString(1, personWhoSendRequest);
        stmt_1.setString(2, username_friend_Second_Col);
        stmt_1.setString(3, status);
        stmt_1.executeUpdate();
        
        String checkQuery = "SELECT * FROM friends WHERE (username_owner = ? AND username_friend = ?) OR (username_owner = ? AND username_friend = ?)";
        stmt_1 = getConnection().prepareStatement(checkQuery);
        stmt_1.setString(1, username_friend_Second_Col);
        stmt_1.setString(2, personWhoSendRequest);
        stmt_1.setString(3, personWhoSendRequest);
        stmt_1.setString(4, username_friend_Second_Col);
        rs = stmt_1.executeQuery();
        if (rs.next()) {

            return false;
        }

        String query_1 = "INSERT INTO friends (username_owner , username_friend ) VALUES (?, ?)";
        String query_2 = "INSERT INTO friends (username_owner , username_friend ) VALUES (?, ?)";

        stmt_1 = getConnection().prepareStatement(query_1);
        stmt_2 = getConnection().prepareStatement(query_2);

        stmt_1.setString(1, username_friend_Second_Col);
        stmt_1.setString(2, personWhoSendRequest);
        rowAffected = stmt_1.executeUpdate();

        stmt_2.setString(1, personWhoSendRequest);
        stmt_2.setString(2, username_friend_Second_Col);
        rowAffected += stmt_2.executeUpdate();

        String description = "You and " + personWhoSendRequest + " are now friends";
        PreparedStatement insertStmt = getConnection().prepareStatement(insertQuery);
        insertStmt.setString(1, username_friend_Second_Col);
        insertStmt.setString(2, description);
        insertStmt.executeUpdate();

        description = "You and " + username_friend_Second_Col + " are now friends";
        insertStmt.setString(1, personWhoSendRequest);
        insertStmt.setString(2, description);
        insertStmt.executeUpdate();

    } catch (SQLException ex) {
        return false;
    }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt_1 != null) {
            stmt_1.close();
        }if (stmt_2 != null) {
            stmt_2.close();
        }
    }

    return rowAffected > 0;
}

public boolean removeRowFromFriendRequest(String Username_Friend_Second_Col , String PersonWHOsendRequest , String StatuS) throws SQLException{
        String del = "delete from friendrequest where username_owner = ? and username_friend = ? and status =? ";
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            int rowAffected =0; 
    try {
            stmt = getConnection().prepareStatement(del);
            stmt.setString(1, PersonWHOsendRequest);
            stmt.setString(2, Username_Friend_Second_Col);
            stmt.setString(3, StatuS);    
            rowAffected = stmt.executeUpdate();
                
     } catch (SQLException ex) {
        return false;
     }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
    
           return rowAffected > 0;

}
public boolean sendFriendRequest(String username_owner, String username_friend , String status) throws SQLException {
        boolean flag=true;
        String sel = "select username_owner , username_friend , status from friendrequest where username_owner =? and username_friend = ? and status =? ";
            PreparedStatement stmt = null;
            ResultSet rs = null;            
        try {
            stmt = getConnection().prepareStatement(sel);
            stmt.setString(1, username_owner);
            stmt.setString(2, username_friend);
            stmt.setString(3, status);
            rs = stmt.executeQuery();
            if(rs.next())
                flag =false;
            
            else{
                String query = "INSERT INTO friendrequest (username_owner , username_friend , status) VALUES (?, ?, ?)";
 
                stmt = getConnection().prepareStatement(query);
                stmt.setString(1, username_owner);
                stmt.setString(2, username_friend);
                stmt.setString(3, status);
                stmt.executeUpdate();
                flag =true;
                String notificationQuery = "INSERT INTO NotificationHandling (username_owner, description) VALUES (?, ?)";
                PreparedStatement notificationStmt  = getConnection().prepareStatement(notificationQuery);
                notificationStmt.setString(1, username_friend);
                String description = username_owner + " has sent you a friend request.";
                notificationStmt.setString(2, description);
                notificationStmt.executeUpdate();
             }
              
        } catch (SQLException ex) {
            return false;
        }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
           return flag;
              
    }	
	
public String newFriendData (String friendName , String userName) throws SQLException{
    String query  = "SELECT username , firstname , lastname  FROM Userlogins "
                  + "WHERE username NOT IN (SELECT username_friend FROM friends WHERE username_owner = ?) " 
                  + "AND username != ?" + "AND UPPER(firstname) = UPPER(?)";
    
    String total="";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
        try {
            stmt = getConnection().prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setString(2, userName);
            stmt.setString(3, friendName);
            rs = stmt.executeQuery();
            while (rs.next()) {

                String username = rs.getString("username");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                total = total + username + ":" + firstName + ":" + lastName + "#";

            }
            return total;
        } catch (SQLException ex) {
             ex.printStackTrace();
            return "fail";

        }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }

    }
}
public boolean deleteFriend(String username_owner, String username_friend) throws SQLException {
	String query = "DELETE FROM friends WHERE (username_owner = ? AND username_friend = ?) OR (username_owner = ? AND username_friend = ?)";        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            stmt = getConnection().prepareStatement(query);
            stmt.setString(1, username_owner);
            stmt.setString(2, username_friend);
            stmt.setString(3, username_friend);
            stmt.setString(4, username_owner);
            rowsAffected = stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
        return rowsAffected > 0 ;
    }
    
public String showFriendList(String username) throws SQLException {
        String query = "SELECT f.USERNAME_FRIEND , u.firstname, u.lastname, u.birthdate , u.phone  \n"
                + "FROM friends f \n"
                + "JOIN Userlogins u ON f.username_friend = u.username  \n"
                + "WHERE f.username_owner = ?";

        String total = "";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {

                String userFriend = rs.getString("USERNAME_FRIEND");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String birthDate = rs.getString("birthdate");
                String phone = rs.getString("phone");

                total = total + userFriend + ":" + firstName + ":" + lastName + ":" + birthDate + ":" + phone + "#";

            }
            if (total.isEmpty()){
                total = "no";
            }
            return total;
        } catch (SQLException e) {
            e.printStackTrace();
            return "fail";
        }finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }

    }  
public String getNotifications(String username) throws SQLException {
    String query = "SELECT USERNAME_OWNER, DESCRIPTION FROM NotificationHandling WHERE USERNAME_OWNER=? Order by ID DESC";

    PreparedStatement stmt = null;
    ResultSet rs = null;
    String result = "";
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        rs = stmt.executeQuery();
        while (rs.next()) {
            String owner = rs.getString("USERNAME_OWNER");
            String description = rs.getString("DESCRIPTION");
            result += owner + ":" + description + "#";
        }
        return result;
    } catch (SQLException e) {
        e.printStackTrace();
        return "fail";
    } finally {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
    }
}

public String addItem(String username, String ID , String priceItem) throws SQLException {
    int itemID = Integer.parseInt(ID);
    int price = Integer.parseInt(priceItem);
    String checkQuery = "SELECT COUNT(*) FROM WISHLIST_ITEM WHERE USERNAME_OWNER=? AND ID_OF_ITEM=?";
    String insertQuery = "INSERT INTO WISHLIST_ITEM (USERNAME_OWNER, ID_OF_ITEM, REMAINPRICE) Values (?, ?, ? )";
    PreparedStatement checkStmt = null;
    PreparedStatement insertStmt = null;

    ResultSet checkRs = null;
    try {
        checkStmt = getConnection().prepareStatement(checkQuery);
        checkStmt.setString(1, username);
        checkStmt.setInt(2, itemID);
        checkRs = checkStmt.executeQuery();
        if (checkRs.next() && checkRs.getInt(1) > 0) {
            return "fail";
        }
        insertStmt = getConnection().prepareStatement(insertQuery);
        insertStmt.setString(1, username);
        insertStmt.setInt(2, itemID);
        insertStmt.setInt(3, price);
        int affectedRows = insertStmt.executeUpdate();
        if (affectedRows > 0) {
            return "success";
        } else {
            return "fail"; 
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return "fail";
    } finally {
        if (checkRs != null) checkRs.close();
        if (checkStmt != null) checkStmt.close();
        if (insertStmt != null) insertStmt.close();
    }
}


    private String calcMoney(String username) {
        String query = "SELECT CASH FROM USERLOGINS WHERE USERNAME = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String moneyAmount = null;
        try {
            stmt = getConnection().prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int cash = rs.getInt("CASH");
                moneyAmount = Integer.toString(cash);
            } else {
                moneyAmount = "fail";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            moneyAmount = "fail";
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return moneyAmount; 
    }

    private boolean redeemMoney(String username, String currentCash) {
    String query = "UPDATE USERLOGINS SET CASH = ? WHERE USERNAME = ?";
    PreparedStatement stmt = null;
    try {
        int cash = Integer.parseInt(currentCash) ;
         stmt = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);        
        stmt.setInt(1, cash);
        stmt.setString(2, username);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException | NumberFormatException e) {
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    
    private boolean loginFunction(String username, String password) {
    String query = "SELECT * FROM USERLOGINS WHERE USERNAME = ? AND PASSWORD = ?";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private boolean signupFunction(String firstName, String lastName, String username, String phone, String email, String birthdate, String password , String secretanswer) {
    String query = "SELECT * FROM USERLOGINS WHERE USERNAME = ?";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        rs = stmt.executeQuery();
        if (rs.next() ) {
            return false;
        } else {
            String insertQuery = "INSERT INTO USERLOGINS (FIRSTNAME, LASTNAME, USERNAME, PHONE, EMAIL, BIRTHDATE, PASSWORD , SECRETANSWER , CASH) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = getConnection().prepareStatement(insertQuery);
            insertStmt.setString(1, firstName);
            insertStmt.setString(2, lastName);
            insertStmt.setString(3, username);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, email);
            insertStmt.setString(6, birthdate);
            insertStmt.setString(7, password);
            insertStmt.setString(8, secretanswer);
            insertStmt.setInt(9, 0);
            insertStmt.executeUpdate();
            return true ;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 
private boolean editPhoneFunction(String username, String newPhone, String password) {
    String query = "SELECT * FROM USERLOGINS WHERE USERNAME = ? and PASSWORD = ?";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        rs = stmt.executeQuery();
        if (rs.next()) {
            String updateQuery = "UPDATE USERLOGINS SET PHONE = ? WHERE USERNAME = ?";
            PreparedStatement updateStmt = getConnection().prepareStatement(updateQuery);
            updateStmt.setString(1, newPhone);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
            return true;
        } else {
            return false;
        }
    } catch (SQLException e) {
        return false;
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
private boolean editPassFunction(String username, String answerCheck, String passwordChange) {
    String query = "SELECT * FROM USERLOGINS WHERE USERNAME = ? and SECRETANSWER = ?";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        stmt = getConnection().prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, answerCheck);
        rs = stmt.executeQuery();
        if (rs.next() ) {
            String updateQuery = "UPDATE USERLOGINS SET PASSWORD = ? WHERE USERNAME = ?";
            PreparedStatement updateStmt = getConnection().prepareStatement(updateQuery);
            updateStmt.setString(1, passwordChange);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
            return true;
        } else { 
            return false ;
        }
    } catch (SQLException e) {
        return false;
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 
    }
public static void main(String[] args){         
                new Server();
    }
}
