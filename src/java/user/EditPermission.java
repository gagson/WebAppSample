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
import java.io.IOException;
import java.io.InputStream;
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
        String clickedPhoto = request.getParameter("share");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT image_data FROM photos WHERE file_name=?";
                        String selectShare_toString = "SELECT share_to1, share_to2, share_to3 FROM photos WHERE file_name=?";

                        try (PreparedStatement selectMyPhotoStatement = dbConn.prepareStatement(selectMyPhotoString)) {
                            PreparedStatement selectKeywordStatement = dbConn.prepareStatement(selectShare_toString);

                            selectMyPhotoStatement.setString(1, clickedPhoto);
                            selectKeywordStatement.setString(1, clickedPhoto);
                            ResultSet rsMyPhoto = selectMyPhotoStatement.executeQuery();
                            ResultSet rsShare_to = selectKeywordStatement.executeQuery();

                            out.println("<!DOCTYPE html>");
                            out.println("<html>");
                            out.println("<head>\n"
                                    + "        <title>Photo Repository App</title>\n"
                                    + "        <meta charset=\"UTF-8\">\n"
                                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                                    + "        <!-- Latest compiled and minified CSS -->\n"
                                    + "        <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">\n"
                                    + "        <!-- jQuery library -->\n"
                                    + "        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n"
                                    + "        <!-- Popper JS -->\n"
                                    + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n"
                                    + "        <!-- Latest compiled JavaScript -->\n"
                                    + "        <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>\n"
                                    + "    </head>\n"
                                    + "<style>"
                                    + "img {\n"
                                    + "  width: 400px;\n"
                                    + "  height: auto;\n"
                                    + "}</style>"
                                    + "    <body>\n"
                                    + "        <nav class=\"navbar navbar-expand-lg navbar-light bg-light justify-content-center\">\n"
                                    + "            <h2>Photo Repository App</h2>\n"
                                    + "        </nav>\n"
                                    + "        <div class=\"container-fluid\">\n"
                                    + "            <div class=\"h5 text-center\">\n"
                                    + "                Photo Permissions Management\n"
                                    + "            </div>\n"
                                    + "            <div class=\"h3 text-center\">\n"
                                    + "                Please make sure to input the right username!!\n"
                                    + "            </div>\n");
                            out.println("<div><h4><center>You have logged in as <b>" + login
                                    + "</b></center></h4></div>");

                            BufferedImage image = ImageIO.read(rsMyPhoto.getBinaryStream(1));
                            BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                            Graphics2D graphics2D = resizedImage.createGraphics();
                            graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                            graphics2D.dispose();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIO.write(resizedImage, "jpeg", bos);
                            while (rsShare_to.next()) {
                                String share_to1 = rsShare_to.getString(1);
                                String share_to2 = rsShare_to.getString(2);
                                String share_to3 = rsShare_to.getString(3);

                                out.println("            <form action=\"receive_edit_permission\" method=\"post\" enctype=\"multipart/form-data\">\n"
                                        + "                <div class=\"row p-1\"></div>\n"
                                        + "                <div class=\"row\" id=\"newRow\">\n"
                                        + "                    <label class=\"col-md-4\"></label>\n"
                                        + "                    <label class=\"col-md-1\"></label>\n"
                                        + "                    <div class=\"col-md-3\">\n"
                                        + "                        <input type=\"checkbox\" name=\"share_to_public\" class=\"form-check-input\" value=\"share_to_public\">Share to Public"
                                        + "                    </div>\n"
                                        + "                </div>"//row
                                        + "                <div class=\"row p-1\"></div>\n"
                                        + "                <div class=\"row\" id=\"newRow\">\n"
                                        + "                    <label class=\"col-md-4\"></label>\n"
                                        + "                    <label class=\"col-md-1\">Share to:</label>\n"
                                        + "                    <div class=\"col-md-3\">\n"
                                        + "                        <input type=\"text\" name=\"share_to1\" class=\"form-control\" value=\"" + share_to1 + "\">"
                                        + "                    </div>\n"
                                        + "                </div>"//row
                                        + "                <div class=\"row p-1\"></div>\n"
                                        + "                <div class=\"row\" id=\"newRow\">\n"
                                        + "                    <label class=\"col-md-4\"></label>\n"
                                        + "                    <label class=\"col-md-1\">Share to:</label>\n"
                                        + "                    <div class=\"col-md-3\">\n"
                                        + "                        <input type=\"text\" name=\"share_to2\" class=\"form-control\" value=\"" + share_to2 + "\">"
                                        + "                    </div>\n"
                                        + "                </div>"//row
                                        + "                <div class=\"row p-1\"></div>\n"
                                        + "                <div class=\"row\" id=\"newRow\">\n"
                                        + "                    <label class=\"col-md-4\"></label>\n"
                                        + "                    <label class=\"col-md-1\">Share to:</label>\n"
                                        + "                    <div class=\"col-md-3\">\n"
                                        + "                        <input type=\"text\" name=\"share_to3\" class=\"form-control\" value=\"" + share_to3 + "\">"
                                        + "                    </div>\n"
                                        + "                </div>"//row
                                        + "                    <label class=\"col-md-2\"></label>\n"
                                        + "                </div>\n"
                                        + "                <div class=\"row p-1\"></div>\n"
                                        + "                <div class=\"row\" id=\"newRow\">\n"
                                        + "                    <label class=\"col-md-4\"></label>\n"
                                        + "                <div class=\"col-md-4\">\n"
                                        + "                      <img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">"
                                        + "                </div>\n"
                                        + "                </div>\n"
                                        + "                <hr>");
                                out.println("<div class=\"row\">\n"
                                        + "                    <label class=\"col-md-3\"></label>\n"
                                        + "                    <button type=\"submit\" value=\"" + clickedPhoto + "\" name=\"permission\" class=\"col-md-6 btn btn-primary btn-block\">Submit</button>\n"
                                        + "                    <label class=\"col-md-3\"></label>\n"
                                        + "                </div>\n"
                                        + "            </form>");
                            }
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"dashboard\" name=\"dashboard\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"dashboard\">Go back to dashboard</a></div>");
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"Logout\" name=\"logout\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"../logout\">Logout</a></div>");
                            out.println("</body>");
                            out.println("</html>");
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
