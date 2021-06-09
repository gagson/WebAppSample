/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.Part;
import org.sqlite.SQLiteDataSource;
import static user.ReceiveFileServlet.readBytesAndClose;

/**
 *
 * @author gagso
 */
public class ReceiveEditKeyword extends HttpServlet {

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
        String keyword1 = request.getParameter("keyword1");
        String keyword2 = request.getParameter("keyword2");
        String keyword3 = request.getParameter("keyword3");
        String editPhoto = request.getParameter("edit");
        
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
           
            SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
            if (dataSource != null) {
                try (Connection dbConn = dataSource.getConnection()) {
                    String updateString = "UPDATE photos SET keyword1=?, keyword2=?, keyword3=?  WHERE file_name=? AND user_login=?";
                    try (PreparedStatement updateStatement = dbConn.prepareStatement(updateString)) {
                        updateStatement.setString(1, keyword1);
                        updateStatement.setString(2, keyword2);
                        updateStatement.setString(3, keyword3);
                        updateStatement.setString(4, editPhoto);
                        updateStatement.setString(5, login);
                        updateStatement.executeUpdate();
                    }
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
                response.sendRedirect(request.getContextPath());
            }
        }   else {
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
            Logger.getLogger(ReceiveEditKeyword.class.getName()).log(Level.SEVERE, null, ex);
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
