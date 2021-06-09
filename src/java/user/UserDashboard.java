/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT image_data, file_name FROM photos WHERE user_login=?";
                        String selectSharedWithMeString = "SELECT image_data, user_login, file_name FROM photos "
                                + "WHERE share_to1=? or share_to2=? or share_to3=?;";

                        try (PreparedStatement selectMyPhotoStatement = dbConn.prepareStatement(selectMyPhotoString)) {
                            selectMyPhotoStatement.setString(1, login);
                            ResultSet rsMyPhoto = selectMyPhotoStatement.executeQuery();//user's photo
                            PreparedStatement selectSharedWithMeStatement = dbConn.prepareStatement(selectSharedWithMeString);
                            selectSharedWithMeStatement.setString(1, login);
                            selectSharedWithMeStatement.setString(2, login);
                            selectSharedWithMeStatement.setString(3, login);
                            ResultSet rsSharedPhoto = selectSharedWithMeStatement.executeQuery();//photo shared to user

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
                            out.println("</head>"
                                    + "<style>"
                                    + "img {\n"
                                    + "  width: 100px;\n"
                                    + "  height: auto;\n"
                                    + "}</style>");
                            out.println("<body>");
                            out.println("<nav class=\"navbar navbar-expand-lg navbar-light bg-light justify-content-center\">\n"
                                    + "            <h2>Photo Repository App</h2>\n"
                                    + "        </nav>");
                            out.println("           <div class=\"h3 text-center\">\n"
                                    + "                Dashboard - Manage your photos\n"
                                    + "            </div>");
                            out.println("<div><h5><center>You have logged in as <b>" + login
                                    + "</b></center></h5></div>");
                            out.println("<center>(Your homefolder is " + homeFolder + ")</center>");
                            out.println("<hr>");//how to separate it
                            out.println(" <form action=\"download\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>My Photos</p>");
                            int i = 1;
                            while (rsMyPhoto.next()) {
                                String filename = rsMyPhoto.getString(2);
                                BufferedImage image = ImageIO.read(rsMyPhoto.getBinaryStream(1));
                                BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics2D = resizedImage.createGraphics();
                                graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                                graphics2D.dispose();
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ImageIO.write(resizedImage, "jpeg", bos);
                                out.println("<div class=\"row\">"
                                        + "<label class=\"col-md-1\"></label>");
                                out.println("<span>\n");
                                out.println(" <label class=\"form-check-label\">");
                                out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedMyPhoto\" class=\"form-check-input\" value=\"" + filename + "\">" + i
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               

//                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
//                                out.println("<p>(" + filename + ")</p>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='edit';\" name=\"edit\" class=\"btn btn-outline-success\">Edit</button>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='permissions';\" name=\"share\" class=\"btn btn-outline-info\">Share</button>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                i++;
                            }
                            out.println("</div>");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Shared with Me</p>");
                            int j = 1;
                            while (rsSharedPhoto.next()) {
                                String shared_from = rsSharedPhoto.getString(2);
                                String filename = rsSharedPhoto.getString(3);
                                BufferedImage image = ImageIO.read(rsSharedPhoto.getBinaryStream(1));
                                BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics2D = resizedImage.createGraphics();
                                graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                                graphics2D.dispose();
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ImageIO.write(resizedImage, "jpeg", bos);
                                out.println("<div class=\"row\">"
                                        + "<label class=\"col-md-1\"></label>");
                                out.println("<span>\n");
                                out.println(" <label class=\"form-check-label\">\n");
                                out.println(" <input type=\"checkbox\" id=\"check\" name=\"checkedShare\" class=\"form-check-input\" value=\"" + filename + "\">" + j
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\" width=\"100px\" height=\"auto\">");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<label class=\"col-md-4\">Shared from: <i>" + shared_from + "</i></label>");

//                                out.println("<img src=\"" + path + "/imageFolder/" + shared_from + "/" + filename + "\">");
                                out.println("<div class=\"row p-1\"></div>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                j++;
                            }
                            out.println("</div>");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("</div>");//row
                            out.println("<hr>");
                            out.println("<div class=\"row\">\n"
                                    + "<label class=\"col-md-3\"></label>\n"
                                    + "<input type=\"submit\" value=\"Download Selected Image\" name=\"action\" class=\"col-md-2 btn btn-primary btn-block\">\n"
                                    + "<label class=\"col-md-2\"></label>"
                                    + "<input type=\"submit\" value=\"Delete Selected Image\" onclick=\"form.action='receive_removeImage';\" name=\"action\" class=\"col-md-2 btn btn-danger btn-block\">\n"
                                    + "<label class=\"col-md-3\"></label>\n");
                            out.println("<div class=\"row p-1\"></div>"
                                    + "<label class=\"col-md-7\"></label>"
                                    + "<p>(Only you owned images could be deleted)</p>");
                            out.println("</form>"
                                    + " </div>");
                            out.println("<div class=\"row p-1\"></div>"
                                    + "<div class=\"row\"><label class=\"col-md-8\"></label>"
                                    + "<button value=\"Respond\" name=\"respond\" class=\"col-md-2 btn btn-success btn-block\"><a href=\"./respond\">See requests here</a></button></div>");
                            out.println("<div class=\"row p-1\"></div>"
                                    + "<div class=\"row\"><label class=\"col-md-8\"></label>"
                                    + "<button value=\"Search Photos by Keywords\" name=\"search\" class=\"col-md-2 btn btn-warning btn-block\"><a href=\"../search\">Search Photos by Keywords</a></button></div>");
                            out.println("<div class=\"row p-1\"></div>"
                                    + "<div class=\"row\"><label class=\"col-md-8\"></label>"
                                    + "<button value=\"Upload New Image\" name=\"upload\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./upload\">Upload New Image</a></button></div>");
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
