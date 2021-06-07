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
public class ReceiveChangeStatus extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        String homeFolder = (String) session.getAttribute("homeFolder");

        String statusUsername = request.getParameter("status");
//        String username = request.getParameter("edit"); //the username
//        String originalStatus = request.getParameter("originalStatus");
//        String status = request.getParameter("status");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String updateStatus = "UPDATE credential SET status=?  WHERE login=?";
                        String confirmStatus = "SELECT status FROM credential WHERE login=?";
                        try (PreparedStatement updateStatusStatement = dbConn.prepareStatement(updateStatus)) {
                            PreparedStatement confirmStatusStatement = dbConn.prepareStatement(confirmStatus);
                            confirmStatusStatement.setString(1, statusUsername);
                            ResultSet rsStatus = confirmStatusStatement.executeQuery();
                            String status = rsStatus.getString(1);
                            if (!statusUsername.equals(login)) {
                                if (status.equals("active")) {
                                    updateStatusStatement.setString(1, "disabled");
                                    updateStatusStatement.setString(2, statusUsername);
                                    updateStatusStatement.executeUpdate();
                                } else {
                                    updateStatusStatement.setString(1, "active");
                                    updateStatusStatement.setString(2, statusUsername);
                                    updateStatusStatement.executeUpdate();
                                }
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
                            } else {
                                out.println("<!DOCTYPE html>");
                                out.println("<html>");
                                out.println("<head>");
                                out.println("<title>Photo Repository App</title>");
                                out.println("</head>");
                                out.println("<body>");
                                out.println("<h1>You cannot disable yourself!!</h1>");
                                out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
                                out.println("</body>");
                                out.println("</html>");
                            }
                        }
                    }
                }
            }
            /* TODO output your page here. You may use following sample code. */

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
            Logger.getLogger(ReceiveChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
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
