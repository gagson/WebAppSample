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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.sqlite.SQLiteConfig;
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
        String[] keyword = request.getParameterValues("keyword");
        String[] public_keyword = request.getParameterValues("kw");
        String boolean1 = request.getParameter("boolean1");
        String boolean2 = request.getParameter("boolean2");

        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) { //logged in version
                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectMyPhotoString = "SELECT image_data, file_name FROM photos WHERE (keyword1=? " + boolean1 + " keyword2=? " + boolean2 + " keyword3=?) and (user_login=?)";

                        String selectOtherPhotoString = "SELECT file_name, user_login FROM photos WHERE (keyword1=? " + boolean1 + " keyword2=? " + boolean2 + " keyword3=?)";
                        try (PreparedStatement selectMyPhotoStatement = dbConn.prepareStatement(selectMyPhotoString)) {
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
                            out.println(" <form action=\"user/download\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Owned by You</p>");

                            int j = 1;
                            for (int i = 0; i < keyword.length; i++) {
                                if (keyword[i] != null && !"".equals(keyword[i])) {
                                    selectMyPhotoStatement.setString(1, keyword[i]);
                                    selectMyPhotoStatement.setString(2, keyword[i]);
                                    selectMyPhotoStatement.setString(3, keyword[i]);
                                    selectMyPhotoStatement.setString(4, login);
                                    ResultSet rsMyPhoto = selectMyPhotoStatement.executeQuery();

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
                                        out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedMyPhoto\" class=\"form-check-input\" value=" + filename + ">" + j
                                                + ") </label>\n"
                                                + " </span>");
                                        out.println("<label class=\"col-sm\"></label>");
                                        out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               
                                        out.println("<p>(" + filename + ")</p>");
                                        out.println("<label class=\"col-sm\"></label>");
                                        out.println("</div>");//row
                                        out.println("<div class=\"row p-1\"></div>");
                                        j++;
                                        break;
                                    }
                                }

                            }

                            PreparedStatement selectOtherStatement = dbConn.prepareStatement(selectOtherPhotoString);

                            out.println("</div>");//Owned by others part
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Shared with You</p>");
                            int k = 1;
                            for (int i = 0; i < keyword.length; i++) {
                                if (keyword[i] != null && !"".equals(keyword[i])) {
                                    selectOtherStatement.setString(1, keyword[i]);
                                    selectOtherStatement.setString(2, keyword[i]);
                                    selectOtherStatement.setString(3, keyword[i]);
                                }
                                ResultSet rsOtherPhoto = selectOtherStatement.executeQuery();
                                while (rsOtherPhoto.next()) {
                                    String owned_by = rsOtherPhoto.getString(2);
                                    String filename = rsOtherPhoto.getString(1);
                                    out.println("<div class=\"row\">"
                                            + "<label class=\"col-md-1\"></label>");
                                    out.println("<span>\n");
                                    out.println(" <label class=\"form-check-label\">\n");
                                    out.println(" <input type=\"checkbox\" id=\"check\" name=\"checkedShare\" class=\"form-check-input\" value=\"selection\">" + k
                                            + ") </label>\n"
                                            + " </span>");
                                    out.println("<img src=\"\">");
                                    out.println("<label class=\"col-md-1\"></label>");
                                    out.println("<label class=\"col-md-1\">Owned by: <i>" + owned_by + "</i> ("
                                            + filename + ")</label>");
                                    out.println("<div class=\"row p-1\"></div>");
                                    out.println("</div>");//row
                                    out.println("<div class=\"row p-1\"></div>");
                                    k++;
                                    break;
                                }

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
            } else { //public version

                SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
                if (dataSource != null) {
                    try (Connection dbConn = dataSource.getConnection()) {
                        String selectPublicPhotoString = "SELECT image_data, file_name FROM photos WHERE (keyword1=? " + boolean1 + " keyword2=? " + boolean2 + " keyword3=?) and (share_to_public='public')";

                        try (PreparedStatement selectPublicPhotoStatement = dbConn.prepareStatement(selectPublicPhotoString)) {
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
                            out.println("<div><h4><center>You are in Public page.</b></center></h4></div>");
                            out.println("<hr>");//how to separate it
                            out.println(" <form action=\"public_download\" method=\"post\" enctype=\"multipart/form-data\">");
                            out.println("<div class=\"row\">");
                            out.println("<label class=\"col-md-1\"></label>");
                            out.println("<div id=\"download_form\" name=\"download_form\" class=\"col-md container p-3 my-3 border border-primary rounded\">");
                            out.println("<p>Photos Available to Public:</p>");

                            int j = 1;
                            for (int i = 0; i < public_keyword.length; i++) {
                                if (public_keyword[i] != null && !"".equals(public_keyword[i])) {
                                    selectPublicPhotoStatement.setString(1, public_keyword[i]);
                                    selectPublicPhotoStatement.setString(2, public_keyword[i]);
                                    selectPublicPhotoStatement.setString(3, public_keyword[i]);
                                    ResultSet rs = selectPublicPhotoStatement.executeQuery();

                                    while (rs.next()) {
                                        String filename = rs.getString(2);
                                        BufferedImage image = ImageIO.read(rs.getBinaryStream(1));
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
                                        out.println(" <input type=\"checkbox\" id=\"checkedMyPhoto\" name=\"checkedPublicPhoto\" class=\"form-check-input\" value=" + filename + ">" + j
                                                + ") </label>\n"
                                                + " </span>");
                                        out.println("<label class=\"col-sm\"></label>");
                                        out.println("<img src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray()) + "\">");// image                               
                                        out.println("<p>(" + filename + ")</p>");
                                        out.println("<label class=\"col-sm\"></label>");
                                        out.println("</div>");//row
                                        out.println("<div class=\"row p-1\"></div>");
                                        j++;
                                        break;
                                    }
                                }
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
                            out.println("<button value=\"search\" name=\"search\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./search\">Go back to Photo Search</a></div>");
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

    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        ServletContext application = getServletContext();
        try {
            Class.forName("org.sqlite.JDBC");
            //make sure class JDBC is available
            SQLiteConfig config = new SQLiteConfig();
            //config sqlite variables
            config.setPragma(SQLiteConfig.Pragma.FOREIGN_KEYS, "on");
            //using this object to enforce foreign key
            SQLiteDataSource dataSource = new SQLiteDataSource(config);
            //connect to db
            dataSource.setUrl("jdbc:sqlite:C:\\Users\\gagso\\Documents\\NetBeansProjects\\Photo-Repository\\web\\PhotoRepository.db3"); //db file
            application.setAttribute("dataSource", dataSource);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Validate.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(photoSearchResults.class.getName()).log(Level.SEVERE, null, ex);
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
