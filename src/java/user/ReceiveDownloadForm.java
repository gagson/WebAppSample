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
public class ReceiveDownloadForm extends HttpServlet {

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
        String[] downloadImages = request.getParameterValues("checkedMyPhoto");
        String[] downloadSharedImages = request.getParameterValues("checkedShare");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) {

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectPhotoString = "SELECT image_data FROM photos WHERE file_name=?";
                        String selectSharedPhotoString = "SELECT image_data FROM photos WHERE file_name=? AND (share_to1=? or share_to2=? or share_to3=?)";
                        try (PreparedStatement downloadStatement = dbConn.prepareStatement(selectPhotoString)) {
                            PreparedStatement downloadSharedStatement = dbConn.prepareStatement(selectSharedPhotoString);

                            out.println("<!DOCTYPE html>");
                            out.println("<html>");
                            out.println("<head>");
                            out.println("<title>Photo Repository App</title>");
                            out.println("</head>"
                                    + "<style>"
                                    + "img {\n"
                                    + "  width: auto;\n"
                                    + "  height: auto;\n"
                                    + "}</style>");
                            out.println("<body>");
                            if (downloadImages != null) {
                                for (int i = 0; i < downloadImages.length; i++) {
                                    downloadStatement.setString(1, downloadImages[i]);
                                    ResultSet rs = downloadStatement.executeQuery();
                                    while (rs.next()) {
                                        BufferedImage image = ImageIO.read(rs.getBinaryStream(1));
                                        BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                                        Graphics2D graphics2D = resizedImage.createGraphics();
                                        graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                                        graphics2D.dispose();
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        ImageIO.write(resizedImage, "jpeg", bos);
                                        out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\" download>");// image                               
                                        out.println("<div class=\"row p-1\"></div>");
                                        break;
                                    }
                                }
                            }
                            if (downloadSharedImages != null) {
                                for (int i = 0; i < downloadSharedImages.length; i++) {
                                    downloadSharedStatement.setString(1, downloadSharedImages[i]);
                                    downloadSharedStatement.setString(2, login);
                                    downloadSharedStatement.setString(3, login);
                                    downloadSharedStatement.setString(4, login);
                                    ResultSet rs = downloadSharedStatement.executeQuery();
                                    while (rs.next()) {
                                        BufferedImage image = ImageIO.read(rs.getBinaryStream(1));
                                        BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
                                        Graphics2D graphics2D = resizedImage.createGraphics();
                                        graphics2D.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
                                        graphics2D.dispose();
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        ImageIO.write(resizedImage, "jpeg", bos);
                                        out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               
                                        out.println("<div class=\"row p-1\"></div>");
                                        break;
                                    }
                                }
                            }
                            out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
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
            Logger.getLogger(ReceiveDownloadForm.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReceiveDownloadForm.class.getName()).log(Level.SEVERE, null, ex);
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
