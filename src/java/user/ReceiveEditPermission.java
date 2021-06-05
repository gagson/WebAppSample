/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class ReceiveEditPermission extends HttpServlet {

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
        String share_to1 = request.getParameter("share_to1");
        String share_to2 = request.getParameter("share_to2");
        String share_to3 = request.getParameter("share_to3");
        String editPhoto = request.getParameter("permission");
        String[] share_to_public = request.getParameterValues("share_to_public");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String updateString1 = "UPDATE photos SET share_to1=?  WHERE file_name=?";
                        String updateString2 = "UPDATE photos SET share_to2=?  WHERE file_name=?";
                        String updateString3 = "UPDATE photos SET share_to3=?  WHERE file_name=?";
                        String updateString1null = "UPDATE photos SET share_to1=null  WHERE file_name=?";
                        String updateString2null = "UPDATE photos SET share_to2=null  WHERE file_name=?";
                        String updateString3null = "UPDATE photos SET share_to3=null  WHERE file_name=?";
                        String selectAllUsers = "SELECT login FROM credential";
                        try (PreparedStatement updateStatement1 = dbConn.prepareStatement(updateString1)) {
                            PreparedStatement selectAllUsersStatement = dbConn.prepareStatement(selectAllUsers);
                            ResultSet rsAllUsers = selectAllUsersStatement.executeQuery();
                            PreparedStatement updateStatement2 = dbConn.prepareStatement(updateString2);
                            PreparedStatement updateStatement3 = dbConn.prepareStatement(updateString3);
                            PreparedStatement updateStatement1null = dbConn.prepareStatement(updateString1null);
                            PreparedStatement updateStatement2null = dbConn.prepareStatement(updateString2null);
                            PreparedStatement updateStatement3null = dbConn.prepareStatement(updateString3null);
                            while (rsAllUsers.next()) {//not null
                                if (share_to1 != null) {
                                    if (rsAllUsers.getString(1).equals(share_to1) && rsAllUsers.getString(1) != null) {
                                        updateStatement1.setString(1, share_to1);
                                        updateStatement1.setString(2, editPhoto);
                                        updateStatement1.executeUpdate();
                                    }
                                } else {
                                    updateStatement1null.setString(1, editPhoto);
                                    updateStatement1null.executeUpdate();
                                }
                                if (share_to2 != null) {
                                    if (rsAllUsers.getString(1).equals(share_to2) && rsAllUsers.getString(1) != null) {
                                        updateStatement2.setString(1, share_to2);
                                        updateStatement2.setString(2, editPhoto);
                                        updateStatement2.executeUpdate();
                                    }
                                } else {
                                    updateStatement2null.setString(1, editPhoto);
                                    updateStatement2null.executeUpdate();
                                }
                                if (share_to3 != null) {
                                    if (rsAllUsers.getString(1).equals(share_to3) && rsAllUsers.getString(1) != null) {
                                        updateStatement3.setString(1, share_to3);
                                        updateStatement3.setString(2, editPhoto);
                                        updateStatement3.executeUpdate();
                                    }
                                } else {
                                    updateStatement3null.setString(1, editPhoto);
                                    updateStatement3null.executeUpdate();
                                }
                            }
                        }
                        if (share_to_public != null) {
                            String updatePublic = "UPDATE photos SET share_to_public='public' WHERE file_name=?";
                            PreparedStatement updatePublicStatement = dbConn.prepareStatement(updatePublic);
                            updatePublicStatement.setString(1, editPhoto);
                            updatePublicStatement.executeUpdate();
                        } else {
                            String delPublic = "UPDATE photos SET share_to_public=null WHERE file_name=?";
                            PreparedStatement delPublicStatement = dbConn.prepareStatement(delPublic);
                            delPublicStatement.setString(1, editPhoto);
                            delPublicStatement.executeUpdate();
                        }

                    }
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>File Storage</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>Success!!</h1>");
                    out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
                    out.println("</body>");
                    out.println("</html>");

                } else {
                    response.sendRedirect(request.getContextPath());
                }
            } else {
                response.sendRedirect(request.getContextPath());
            }
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
            Logger.getLogger(ReceiveEditKeyword.class
                    .getName()).log(Level.SEVERE, null, ex);
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
