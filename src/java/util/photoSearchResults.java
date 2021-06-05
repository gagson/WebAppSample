/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
public class photoSearchResults extends HttpServlet {

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
        String keyword1 = request.getParameter("keyword1");
        String keyword2 = request.getParameter("keyword2");
        String keyword3 = request.getParameter("keyword3");
        String public_keyword1 = request.getParameter("kw1");
        String public_keyword2 = request.getParameter("kw2");
        String public_keyword3 = request.getParameter("kw3");
        
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) { //logged in version
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString1 = "SELECT image_data, file_name FROM photos WHERE (keyword1=? or keyword2=? or keyword3=?) and (user_login=?)";
                        String selectMyPhotoString2 = "SELECT image_data, file_name FROM photos WHERE (keyword1=? or keyword2=? or keyword3=?) and (user_login=?)";
                        String selectMyPhotoString3 = "SELECT image_data, file_name FROM photos WHERE (keyword1=? or keyword2=? or keyword3=?) and (user_login=?)";
                        String selectSharedWithMeString1 = "SELECT image_data, user_login, file_name FROM photos "
                                + "WHERE (share_to1=? or share_to2=? or share_to3=?) and (keyword1=? or keyword2=? or keyword3=?)";
                        String selectSharedWithMeString2 = "SELECT image_data, user_login, file_name FROM photos "
                                + "WHERE (share_to1=? or share_to2=? or share_to3=?) and (keyword1=? or keyword2=? or keyword3=?)";
                        String selectSharedWithMeString3 = "SELECT image_data, user_login, file_name FROM photos "
                                + "WHERE (share_to1=? or share_to2=? or share_to3=?) and (keyword1=? or keyword2=? or keyword3=?)";
                        
                        try (PreparedStatement selectMyPhotoStatement1 = dbConn.prepareStatement(selectMyPhotoString1)) {
                            selectMyPhotoStatement1.setString(1, keyword1);
                            selectMyPhotoStatement1.setString(2, keyword1);
                            selectMyPhotoStatement1.setString(3, keyword1);
                            selectMyPhotoStatement1.setString(4, login);
                            ResultSet rsMyPhoto1 = selectMyPhotoStatement1.executeQuery();//user's photo
                            PreparedStatement selectMyPhotoStatement2 = dbConn.prepareStatement(selectMyPhotoString2);
                            selectMyPhotoStatement2.setString(1, keyword2);
                            selectMyPhotoStatement2.setString(2, keyword2);
                            selectMyPhotoStatement2.setString(3, keyword2);
                            selectMyPhotoStatement2.setString(4, login);
                            ResultSet rsMyPhoto2 = selectMyPhotoStatement2.executeQuery();
                            PreparedStatement selectMyPhotoStatement3 = dbConn.prepareStatement(selectMyPhotoString3);
                            selectMyPhotoStatement3.setString(1, keyword3);
                            selectMyPhotoStatement3.setString(2, keyword3);
                            selectMyPhotoStatement3.setString(3, keyword3);
                            selectMyPhotoStatement3.setString(4, login);
                            ResultSet rsMyPhoto3 = selectMyPhotoStatement3.executeQuery();
                            
                            PreparedStatement selectSharedWithMeStatement1 = dbConn.prepareStatement(selectSharedWithMeString1);
                            selectSharedWithMeStatement1.setString(1, login);
                            selectSharedWithMeStatement1.setString(2, login);
                            selectSharedWithMeStatement1.setString(3, login);
                            selectSharedWithMeStatement1.setString(4, keyword1);
                            selectSharedWithMeStatement1.setString(5, keyword1);
                            selectSharedWithMeStatement1.setString(6, keyword1);
                            ResultSet rsSharedPhoto1 = selectSharedWithMeStatement1.executeQuery();//photo shared to user
                            PreparedStatement selectSharedWithMeStatement2 = dbConn.prepareStatement(selectSharedWithMeString2);
                            selectSharedWithMeStatement2.setString(1, login);
                            selectSharedWithMeStatement2.setString(2, login);
                            selectSharedWithMeStatement2.setString(3, login);
                            selectSharedWithMeStatement2.setString(4, keyword2);
                            selectSharedWithMeStatement2.setString(5, keyword2);
                            selectSharedWithMeStatement2.setString(6, keyword2);
                            ResultSet rsSharedPhoto2 = selectSharedWithMeStatement2.executeQuery();
                            PreparedStatement selectSharedWithMeStatement3 = dbConn.prepareStatement(selectSharedWithMeString3);
                            selectSharedWithMeStatement3.setString(1, login);
                            selectSharedWithMeStatement3.setString(2, login);
                            selectSharedWithMeStatement3.setString(3, login);
                            selectSharedWithMeStatement3.setString(4, keyword3);
                            selectSharedWithMeStatement3.setString(5, keyword3);
                            selectSharedWithMeStatement3.setString(6, keyword3);
                            ResultSet rsSharedPhoto3 = selectSharedWithMeStatement3.executeQuery();

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
                            out.println("           <div class=\"h5 text-center\">\n"
                                    + "                Search Results Page\n"
                                    + "            </div>");
                            out.println("<div><h4><center>You have logged in as <b>" + login
                                    //+ "(type:" + type + ")" 
                                    + "</b></center></h4></div>");
                            out.println("<center>(Your homefolder is " + homeFolder + ")</center>");
                            out.println("<hr>");//how to separate it
                            out.println(" <form action=\"upload\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Owned by You</p>");
                            int i = 1;
                            while (rsMyPhoto1.next()) {
                                String filename = rsMyPhoto1.getString(2);
//                                String path = request.getContextPath();

                                //Read the image data from the db, resize the image, and place a base64 string
                                BufferedImage image = ImageIO.read(rsMyPhoto1.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedMyPhoto\" class=\"form-check-input\" value=" + filename + ">" + i
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               

//                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
                                out.println("<p>(" + filename + ")</p>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='edit';\" name=\"edit\" class=\"btn btn-outline-success\">Edit</button>");
//                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='permissions';\" name=\"share\" class=\"btn btn-outline-info\">Share</button>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                i++;
                            }
                            while (rsMyPhoto2.next()) {
                                String filename = rsMyPhoto2.getString(2);
//                                String path = request.getContextPath();

                                //Read the image data from the db, resize the image, and place a base64 string
                                BufferedImage image = ImageIO.read(rsMyPhoto2.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedMyPhoto\" class=\"form-check-input\" value=" + filename + ">" + i
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               

//                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
                                out.println("<p>(" + filename + ")</p>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='edit';\" name=\"edit\" class=\"btn btn-outline-success\">Edit</button>");
//                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='permissions';\" name=\"share\" class=\"btn btn-outline-info\">Share</button>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                i++;
                            }
                            while (rsMyPhoto3.next()) {
                                String filename = rsMyPhoto3.getString(2);
//                                String path = request.getContextPath();

                                //Read the image data from the db, resize the image, and place a base64 string
                                BufferedImage image = ImageIO.read(rsMyPhoto3.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedMyPhoto\" class=\"form-check-input\" value=" + filename + ">" + i
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               

//                                out.println("<img src=\"" + path + "/imageFolder/" + login + "/" + filename + "\">");
                                out.println("<p>(" + filename + ")</p>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='edit';\" name=\"edit\" class=\"btn btn-outline-success\">Edit</button>");
//                                out.println("<label class=\"col-sm\"></label>");
                                out.println("<button type=\"submit\" value=\"" + filename + "\" onclick=\"form.action='permissions';\" name=\"share\" class=\"btn btn-outline-info\">Share</button>");
                                out.println("<label class=\"col-sm\"></label>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                i++;
                            }
                            out.println("</div>");//Shared part
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Shared with You</p>");
                            int j = 1;
                            while (rsSharedPhoto1.next()) {
                                String shared_from = rsSharedPhoto1.getString(2);
                                String filename = rsSharedPhoto1.getString(3);
//                                String path = request.getContextPath();
                                BufferedImage image = ImageIO.read(rsSharedPhoto1.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"check\" name=\"checkedShare\" class=\"form-check-input\" value=\"selection\">" + j
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\" width=\"100px\" height=\"auto\">");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<label class=\"col-md-1\">Shared from: <i>" + shared_from + "</i> ("
                                        + filename + ")</label>");

//                                out.println("<img src=\"" + path + "/imageFolder/" + shared_from + "/" + filename + "\">");
                                out.println("<div class=\"row p-1\"></div>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                j++;
                            }
                            while (rsSharedPhoto2.next()) {
                                String shared_from = rsSharedPhoto2.getString(2);
                                String filename = rsSharedPhoto2.getString(3);
//                                String path = request.getContextPath();
                                BufferedImage image = ImageIO.read(rsSharedPhoto2.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"check\" name=\"checkedShare\" class=\"form-check-input\" value=\"selection\">" + j
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\" width=\"100px\" height=\"auto\">");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<label class=\"col-md-1\">Shared from: <i>" + shared_from + "</i> ("
                                        + filename + ")</label>");

//                                out.println("<img src=\"" + path + "/imageFolder/" + shared_from + "/" + filename + "\">");
                                out.println("<div class=\"row p-1\"></div>");
                                out.println("</div>");//row
                                out.println("<div class=\"row p-1\"></div>");
                                j++;
                            }
                            while (rsSharedPhoto3.next()) {
                                String shared_from = rsSharedPhoto3.getString(2);
                                String filename = rsSharedPhoto3.getString(3);
//                                String path = request.getContextPath();
                                BufferedImage image = ImageIO.read(rsSharedPhoto3.getBinaryStream(1));
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
                                out.println(" <input type=\"checkbox\" id=\"check\" name=\"checkedShare\" class=\"form-check-input\" value=\"selection\">" + j
                                        + ") </label>\n"
                                        + " </span>");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\" width=\"100px\" height=\"auto\">");
                                out.println("<label class=\"col-md-1\"></label>");
                                out.println("<label class=\"col-md-1\">Shared from: <i>" + shared_from + "</i> ("
                                        + filename + ")</label>");

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
                                    + "<label class=\"col-md-5\"></label>\n"
                                    + "<input type=\"submit\" value=\"Download Selected Image\" name=\"action\" class=\"col-md-2 btn btn-primary btn-block\">\n"
                                    + "<label class=\"col-md-5\"></label>");
                            out.println("</form>"
                                    + " </div>");
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"dashboard\" name=\"dashboard\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./user/dashboard\">Go back to dashboard</a></div>");
                            
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"Logout\" name=\"logout\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./logout\">Logout</a></div>");
                            out.println("</body>");
                            out.println("</html>");
                        }
                    }
                } else {
                    response.sendRedirect(request.getContextPath());
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
        response.sendRedirect(request.getContextPath());
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
            Logger.getLogger(photoSearchResults.class.getName()).log(Level.SEVERE, null, ex);
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
