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
public class readRequests extends HttpServlet {

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

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String checkRequest = "SELECT image_data, file_name, request FROM photos WHERE user_login=?";
                        try (PreparedStatement checkRequestStatement = dbConn.prepareStatement(checkRequest)) {
                            checkRequestStatement.setString(1, login);
                            ResultSet rs = checkRequestStatement.executeQuery();
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
                                    + "                Requests Page\n"
                                    + "            </div>");
                            out.println("<div><h5><center>You have logged in as <b>" + login
                                    //+ "(type:" + type + ")" 
                                    + "</b></center></h5></div>");
                            out.println("<hr>");
                            out.println(" <form action=\"receive_respond\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"response_form\" name=\"response_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Request to you:</p>");
                            int j = 1;
                            while (rs.next()) {
                                String requestFrom = rs.getString(3);
                                String filename = rs.getString(2);
                                BufferedImage image = ImageIO.read(rs.getBinaryStream(1));
                                if (requestFrom != null) {
                                    BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                                    Graphics2D graphics2D = resizedImage.createGraphics();
                                    graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                                    graphics2D.dispose();
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    ImageIO.write(resizedImage, "jpeg", bos);
                                    out.println("<div class=\"row\">"
                                            + "<label class=\"col-md-1\">"+ j +")</label>"
                                            + "<label class=\"col-md-4\"><i> " + requestFrom + "</i> Requesting access to the photo (" + filename + ") </label>");
                                    
                                    out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image
                                    out.println("<label class=\"col-sm-1\"></label>");
                                    out.println("<button type=\"submit\" value=\"" + filename + " name=\"respond_grant\" class=\"btn btn-outline-success\">Grant</button>");
                                    out.println("<label class=\"col-sm-1\"></label>");
                                    out.println("<button type=\"submit\" value=\"" + filename + " name=\"respond_ignore\" class=\"btn btn-outline-danger\">Ignore</button>");
                                    out.println("<label class=\"col-sm-1\"></label>");
                                    out.println("</div>");//row
                                    out.println("<div class=\"row p-1\"></div>");
                                    j++;
                                }
                            }
                            out.println("</div></div>");
                            out.println("<hr>");
                            out.println("</form>");
                            out.println("<div class=\"row p-1\"></div>");
                            out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                            out.println("<button value=\"dashboard\" name=\"dashboard\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./dashboard\">Go back to dashboard</a></div>");

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
            Logger.getLogger(readRequests.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(readRequests.class.getName()).log(Level.SEVERE, null, ex);
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
