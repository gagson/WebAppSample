/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author gagso
 */
public class ReceiveAdminForm extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        String homeFolder = (String) session.getAttribute("homeFolder");
        
        String username = request.getParameter("edit"); //the username
        String originalStatus = request.getParameter("originalStatus");
        String status = request.getParameter("status");
        String originalRole = request.getParameter("originalRole");
        String delete = request.getParameter("delete");

        

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String updateRole = "UPDATE credential SET type=?  WHERE login=?";
                        String deleteAccount = "DELETE FROM credential WHERE login=?";
                        String updateStatus = "UPDATE credential SET status=?  WHERE login=?";
                        try (PreparedStatement updateRoleStatement = dbConn.prepareStatement(updateRole)) {
                            if (("ordinary".equals(originalRole)) && ((String) username != "public")) {
                                updateRoleStatement.setString(1, "admin");
                                updateRoleStatement.setString(2, username);
                                updateRoleStatement.executeUpdate();
                            }
                            if ("admin".equals(originalRole)) {
                                updateRoleStatement.setString(1, "ordinary");
                                updateRoleStatement.setString(2, username);
                                updateRoleStatement.executeUpdate();
                            }
                            if (delete != null) {
                                PreparedStatement deleteStatement = dbConn.prepareStatement(deleteAccount);
                                deleteStatement.setString(1, delete);
                                deleteStatement.executeUpdate();
                            }
                            if (("active".equals(originalStatus)) && ((String) username != "public")) {
                                PreparedStatement updateStatusStatement = dbConn.prepareStatement(updateStatus);
                                updateStatusStatement.setString(1, "disabled");
                                updateStatusStatement.setString(2, status);
                                updateStatusStatement.executeUpdate();
                            }
                            if ("disabled".equals(originalStatus)) {
                                PreparedStatement updateStatusStatement = dbConn.prepareStatement(updateStatus);
                                updateStatusStatement.setString(1, "active");
                                updateStatusStatement.setString(2, status);
                                updateStatusStatement.executeUpdate();
                            }
                        }
                        

                        
                    }
                }
            }
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Photo Repository App</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Success!!</h1>");
            out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
            out.println("</body>");
            out.println("</html>");
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
        response.sendRedirect("dashboard");
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
            Logger.getLogger(ReceiveAdminForm.class.getName()).log(Level.SEVERE, null, ex);
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

}
