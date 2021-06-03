/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
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
public class UserDashboard extends HttpServlet {

    protected ArrayList<ArrayList<Object>> processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        String homeFolder = (String) session.getAttribute("homeFolder");
        //session.removeAttribute("userName"); //cannot access to /dashboard directly
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT file_name FROM photos WHERE user_login=?";
                        String selectSharedWithMeString = "SELECT file_name FROM photos "
                                + "WHERE share_to1=? or share_to2=? or share_to3=?;";

                        try (PreparedStatement selectMyPhotoStatement = dbConn.prepareStatement(selectMyPhotoString)) {
                            selectMyPhotoStatement.setString(1, login);
                            ResultSet rsMyPhoto = selectMyPhotoStatement.executeQuery();
                            
                            PreparedStatement selectSharedWithMeStatement = dbConn.prepareStatement(selectSharedWithMeString);
                            selectSharedWithMeStatement.setString(1, login);
                            selectSharedWithMeStatement.setString(2, login);
                            selectSharedWithMeStatement.setString(3, login);
                            ResultSet rsSharedPhoto = selectSharedWithMeStatement.executeQuery();
                            
                            out.println("<!DOCTYPE html>");
                            out.println("<html>");
                            out.println("<head>");
                            out.println("<title>Photo Repository App</title>\n"
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
                                    + "                Dashboard - Manage your photos\n"
                                    + "            </div>");
                            out.println("<div><h5>Welcome " + login
                                    + "(type:" + type + ")" + " to the dashboard</h5></div>");
                            out.println("(Your homefolder is " + homeFolder + ")");
                            out.println("<hr>");
                            out.println(" <form action=\"receive_permission\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>My Photos</p>");
                            while (rsMyPhoto.next()) {
                                String filename = rsMyPhoto.getString(1);
                                String path = request.getContextPath();
                                out.println("<div class=\"row\">"
                                        + "<label class=\"col-md-1\"></label>");
                                out.println("<span>\n");
                                out.println(" <label class=\"form-check-label\">\n");
                                out.println(" <input type=\"checkbox\" id=\"check\" class=\"form-check-input\" value=\"selection\">Select "
                                        + " </label>\n"
                                        + " </span>");
                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
                                out.println("Filename: " + filename);
                                out.println("<label class=\"col-md-1\"></label>");
                                
                                out.println("<div class=\"row p-1\"></div>");
                                out.println("</div>");//row
                            }
                            out.println("</div>");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Shared with Me</p>");
                            while (rsSharedPhoto.next()) {
                                String filename = rsSharedPhoto.getString(1);
                                String path = request.getContextPath();
                                out.println("<div class=\"row\">"
                                        + "<label class=\"col-md-1\"></label>");
                                out.println("<span>\n");
                                out.println(" <label class=\"form-check-label\">\n");
                                out.println(" <input type=\"checkbox\" id=\"check\" class=\"form-check-input\" value=\"selection\">Select "
                                        + " </label>\n"
                                        + " </span>");
                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
                                out.println("Filename: " + filename);
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<div class=\"row p-1\"></div>");
                                out.println("</div>");//row
                            }
                            out.println("</div>");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("</div>");//row
                            out.println("<hr>");
                            out.println("<div class=\"row\">\n"
                                    + "<label class=\"col-md-2\"></label>\n"
                                    + "<input type=\"submit\" value=\"Download Selected Image\" name=\"action\" class=\"col-md-2 btn btn-primary btn-block\">\n"
                                    + "<label class=\"col-md-1\"></label>"
                                    + "<input type=\"submit\" value=\"Delete Selected Image\" name=\"action\" class=\"col-md-2 btn btn-danger btn-block\">\n"
                                    + "<label class=\"col-md-1\"></label>"
                                    + "<button value=\"Upload New Image\" name=\"upload\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./upload\">Upload New Image</a></button>\n"
                                    + "<label class=\"col-md-2\"></label>\n"
                                    + " </div>");
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"Photo Permission Management\" name=\"upload\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./permissions\">Photo Permissions Management</a></button>");
                            out.println("</div>");
                            out.println("</form>");
                            out.println("<div class=\"row\"><label class=\"col-md-10\"></label>");
                            out.println("<a href=\"../logout\">Logout</a></div>");
                            out.println("</body>");
                            out.println("</html>");
                        }
                    }
                } else {
                    response.sendRedirect(request.getContextPath());
                }
            }
        }
        return null;
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
            Logger.getLogger(UserDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(UserDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
