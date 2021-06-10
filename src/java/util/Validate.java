/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author gagso
 */
public class Validate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //Read the credential from the request
        String nameParam = request.getParameter("name");
        String secretParam = request.getParameter("secret");
        //check the param 
        if ((nameParam != null) && (secretParam != null)) {
            //access the context to obtain a data source to SQLite
            ServletContext application = getServletContext();
            SQLiteDataSource dataSource = (SQLiteDataSource) application.getAttribute("dataSource");
            if (dataSource != null) { //check the datasource
                try (Connection dbConn = dataSource.getConnection()) {
                    String selectString = "SELECT login, type, status FROM credential WHERE login=? AND secret=? AND status='active'";
                    try (PreparedStatement selectStatement = dbConn.prepareStatement(selectString)) {
                        selectStatement.setString(1, nameParam);
                        selectStatement.setString(2, secretParam);
                        try (ResultSet matchingUsers = selectStatement.executeQuery()) {
                            if (matchingUsers.next()) {
                                HttpSession session = request.getSession();
                                session.setAttribute("login", nameParam);
                                session.setAttribute("type", matchingUsers.getString("type"));
//                                session.setAttribute("homeFolder", matchingUsers.getString("home_folder"));
                                if (!"disabled".equals(matchingUsers.getString(3))) {
                                    if (matchingUsers.getString("type").equals("admin")) { //if type is "admin"
                                        response.sendRedirect("admin/dashboard");
                                    } else {
                                        response.sendRedirect("user/dashboard");
                                    }
                                } else {
                                    response.sendRedirect(request.getContextPath());
                                }
                            } else { //no matching in the db
                                response.sendRedirect(request.getContextPath());
                            }
                        }
                    }
                }
            } else {
                response.sendRedirect(request.getContextPath());
            }
        } else {
            response.sendRedirect(request.getContextPath());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Validate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Validate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        ServletContext application = getServletContext();
        try {
            Class.forName("org.sqlite.JDBC");
            //make sure class JDBC is available
            SQLiteConfig config = new SQLiteConfig();
            //config sqlite variables
            config.setPragma(SQLiteConfig.Pragma.FOREIGN_KEYS, "on");
            //using this object to enforce foreign key
            SQLiteDataSource dataSource = new SQLiteDataSource(config);
            //connect to db
            dataSource.setUrl("jdbc:sqlite:C:\\Users\\gagso\\Documents\\NetBeansProjects\\Photo-Repository\\web\\PhotoRepository.db3"); //db file
            application.setAttribute("dataSource", dataSource);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Validate.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
