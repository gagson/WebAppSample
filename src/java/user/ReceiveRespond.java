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
public class ReceiveRespond extends HttpServlet {

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
        String grant = request.getParameter("respond_grant");
        String ignore = request.getParameter("respond_ignore");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String grantString1 = "UPDATE photos SET request=null, share_to1=? WHERE file_name=?";
                        String grantString2 = "UPDATE photos SET request=null, share_to2=? WHERE file_name=?";
                        String grantString3 = "UPDATE photos SET request=null, share_to3=? WHERE file_name=?";
                        String ignoreString = "DELETE request FROM photos WHERE file_name=?";
                        String shareString1 = "SELECT share_to1 FROM photos WHERE file_name=?";
                        String shareString2 = "SELECT share_to2 FROM photos WHERE file_name=?";
                        String shareString3 = "SELECT share_to3 FROM photos WHERE file_name=?";

                        try (PreparedStatement grantStatement1 = dbConn.prepareStatement(grantString1)) {
                            PreparedStatement grantStatement2 = dbConn.prepareStatement(grantString2);
                            PreparedStatement grantStatement3 = dbConn.prepareStatement(grantString3);
                            PreparedStatement ignoreStatement = dbConn.prepareStatement(ignoreString);
                            PreparedStatement shareStatement1 = dbConn.prepareStatement(shareString1);
                            PreparedStatement shareStatement2 = dbConn.prepareStatement(shareString2);
                            PreparedStatement shareStatement3 = dbConn.prepareStatement(shareString3);

                            if (ignore != null) {
                                ignoreStatement.setString(1, ignore);
                                ignoreStatement.executeUpdate();
                                out.println("<!DOCTYPE html>");
                                out.println("<html>");
                                out.println("<head>");
                                out.println("<title>Photo Repository App</title>");
                                out.println("</head>");
                                out.println("<body>");
                                out.println("<h1>Ignore Success!!</h1>");
                                out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
                                out.println("</body>");
                                out.println("</html>");
                            } else {
                                shareStatement1.setString(1, grant);
                                ResultSet rs1 = shareStatement1.executeQuery();
                                shareStatement2.setString(1, grant);
                                ResultSet rs2 = shareStatement2.executeQuery();
                                shareStatement3.setString(1, grant);
                                ResultSet rs3 = shareStatement3.executeQuery();

                                if (rs1.getString(1) == null) {
                                    grantStatement1.setString(1, grant);
                                    grantStatement1.executeUpdate();
                                } else if (rs2.getString(1) == null) {
                                    grantStatement2.setString(1, grant);
                                    grantStatement2.executeUpdate();
                                } else if (rs3.getString(1) == null) {
                                    grantStatement3.setString(1, grant);
                                    grantStatement3.executeUpdate();
                                }
                                out.println("<!DOCTYPE html>");
                                out.println("<html>");
                                out.println("<head>");
                                out.println("<title>Photo Repository App</title>");
                                out.println("</head>");
                                out.println("<body>");
                                out.println("<h1>Grant Access Success!!</h1>");
                                out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
                                out.println("</body>");
                                out.println("</html>");
                            }

                        }

                    }
                }
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ReceiveRespond.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReceiveRespond.class.getName()).log(Level.SEVERE, null, ex);
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
