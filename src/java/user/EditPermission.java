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
import java.sql.ResultSet;
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
public class EditPermission extends HttpServlet {

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
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        String homeFolder = (String) session.getAttribute("homeFolder");
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT file_name, share_to1, share_to2, share_to3 FROM photos WHERE user_login=?";

                        try (PreparedStatement selectStatement = dbConn.prepareStatement(selectMyPhotoString)) {
                            selectStatement.setString(1, login);
                            ResultSet rsMyPhoto = selectStatement.executeQuery();

                            out.println("<!DOCTYPE html>");
                            out.println("<html>");
                            out.println("<head>");
                            out.println("<title>Photo Repository App</title>"
                                    + "        <meta charset=\"UTF-8\">\n"
                                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                                    + "        <!-- Latest compiled and minified CSS -->\n"
                                    + "        <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">\n"
                                    + "        <!-- jQuery library -->\n"
                                    + "        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n"
                                    + "        <!-- Popper JS -->\n"
                                    + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n"
                                    + "        <!-- Latest compiled JavaScript -->\n"
                                    + "        <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>");
                            out.println("</head>");
                            out.println("<body>");
                            out.println("<nav class=\"navbar navbar-expand-lg navbar-light bg-light justify-content-center\">\n"
                                    + "            <h2>Photo Repository App</h2>\n"
                                    + "        </nav>");
                            out.println("           <div class=\"h5 text-center\">\n"
                                    + "                Photo Sharing Permissions Management\n"
                                    + "            </div>");
                            while (rsMyPhoto.next()) {
                                String filename = rsMyPhoto.getString(1);
                                String share_to1 = rsMyPhoto.getString(2);
                                String share_to2 = rsMyPhoto.getString(3);
                                String share_to3 = rsMyPhoto.getString(4);
                            }
                            out.println("");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"Go back to Dashboard\" name=\"dashboard\" class=\"col-md-2 btn btn-light btn-block\">"
                                    + "<a href=\"dashboard\">Go back to Dashboard</a></button></div>");
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"Logout\" name=\"logout\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"../logout\">Logout</a></div>");
                            out.println("</body>");
                            out.println("</html>");
                        }
                    }

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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(EditPermission.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(EditPermission.class.getName()).log(Level.SEVERE, null, ex);
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
