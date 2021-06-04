/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

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
public class EditPhotoKeyword extends HttpServlet {

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
        String editPhoto = request.getParameter("edit");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT image_data FROM photos WHERE file_name=?";
                        String selectKeywordString = "SELECT keyword1, keyword2, keyword3 FROM photos WHERE file_name=?";
                        try (PreparedStatement selectMyPhotoStatement = dbConn.prepareStatement(selectMyPhotoString)) {
                            PreparedStatement selectKeywordStatement = dbConn.prepareStatement(selectKeywordString);
                            selectMyPhotoStatement.setString(1, editPhoto);
                            selectKeywordStatement.setString(1, editPhoto);
                            ResultSet rsMyPhoto = selectMyPhotoStatement.executeQuery();
                            ResultSet rsKeywords = selectKeywordStatement.executeQuery();

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
                                    + "  width: 200px;\n"
                                    + "  height: auto;\n"
                                    + "}</style>"
                                    + "    <body>\n"
                                    + "        <nav class=\"navbar navbar-expand-lg navbar-light bg-light justify-content-center\">\n"
                                    + "            <h2>Photo Repository App</h2>\n"
                                    + "        </nav>\n"
                                    + "        <div class=\"container-fluid\">\n"
                                    + "            <div class=\"h5 text-center\">\n"
                                    + "                Photo Keywords Management\n"
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
                            while (rsKeywords.next()) {
                                String keyword1 = rsKeywords.getString(1);
                                String keyword2 = rsKeywords.getString(2);
                                String keyword3 = rsKeywords.getString(3);
                            
                            out.println("            <form action=\"receive_edit_keyword\" method=\"post\" enctype=\"multipart/form-data\">\n"
                                    + "                <div class=\"row p-1\"></div>\n"
                                    + "                <div class=\"row\" id=\"newRow\">\n"
                                    + "                    <label class=\"col-md-4\"></label>\n"
                                    + "                    <label class=\"col-md-1\">Keyword:</label>\n"
                                    + "                    <div class=\"col-md-3\">\n"
                                    + "                        <input type=\"text\" name=\"keyword1\" class=\"form-control\" value=\"" + keyword1 + "\">"
                                    + "                    </div>\n"
                                    + "                </div>"//row
                                    + "                <div class=\"row p-1\"></div>\n"
                                    + "                <div class=\"row\" id=\"newRow\">\n"
                                    + "                    <label class=\"col-md-4\"></label>\n"
                                    + "                    <label class=\"col-md-1\">Keyword:</label>\n"
                                    + "                    <div class=\"col-md-3\">\n"
                                    + "                        <input type=\"text\" name=\"keyword2\" class=\"form-control\" value=\"" + keyword2 +"\">"
                                    + "                    </div>\n"
                                    + "                </div>"//row
                                    + "                <div class=\"row p-1\"></div>\n"
                                    + "                <div class=\"row\" id=\"newRow\">\n"
                                    + "                    <label class=\"col-md-4\"></label>\n"
                                    + "                    <label class=\"col-md-1\">Keyword:</label>\n"
                                    + "                    <div class=\"col-md-3\">\n"
                                    + "                        <input type=\"text\" name=\"keyword3\" class=\"form-control\" value=\"" + keyword3 + "\">"
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
                                    + "                    <button type=\"submit\" value=\""+ editPhoto +"\" name=\"edit\" class=\"col-md-6 btn btn-primary btn-block\">Submit</button>\n"
                                    + "                    <label class=\"col-md-3\"></label>\n"
                                    + "                </div>\n"
                                    + "            </form>");
                            }
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
            Logger.getLogger(EditPhotoKeyword.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(EditPhotoKeyword.class.getName()).log(Level.SEVERE, null, ex);
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
