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
public class photoPermissions extends HttpServlet {

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
        SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
        if (dataSource != null) {
            String filename = request.getParameter("filename");
            String filePath = homeFolder + "\\" + filename;
            try (Connection dbConn = dataSource.getConnection()) {
                    String selectString = "SELECT photo_id WHERE file_name=? AND user_login=?";
                    try (PreparedStatement selectStatement = dbConn.prepareStatement(selectString)) {
                        selectStatement.setString(1, filename);
                        selectStatement.setString(2, login);
                        try (ResultSet matchingPhoto = selectStatement.executeQuery()) {
                            if (matchingPhoto.next()) {
                                
                            }
                        }
                    }
            }
            try (PrintWriter out = response.getWriter()) {
                if ((login != null) && (type != null) && (homeFolder != null)) {
                    out.println("<!DOCTYPE html>\n"
                            + "\n"
                            + "<html>\n"
                            + "    <head>\n"
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
                            + "    <body>\n"
                            + "        <nav class=\"navbar navbar-expand-lg navbar-light bg-light justify-content-center\">\n"
                            + "            <h2>Photo Repository App</h2>\n"
                            + "        </nav>\n"
                            + "        <div class=\"container-fluid\">\n"
                            + "            <div class=\"h5 text-center\">\n"
                            + "                Image Permissions Management Page\n"
                            + "            </div>\n"
                            + "            <form action=\"receive_permission\" method=\"post\" enctype=\"multipart/form-data\">\n"
                            + "				\n"
                            + "                <div class=\"row p-1\">\n"
                            + "				<label class=\"col-md-6\"></label>\n"
                            + "				<div class=\"form-check\">\n"
                            + "                    <span>\n"
                            + "                        <label class=\"form-check-label\">\n"
                            + "                            <input type=\"checkbox\" id=\"public\" class=\"form-check-input\" value=\"Public\">Public\n"
                            + "                        </label>\n"
                            + "                    </span>\n"
                            + "                </div>\n"
                            + "				</div>\n"
                            + "                <div class=\"row\" id=\"newRow\">\n"
                            + "                    <label class=\"col-md-1\"></label>\n"
                            + "                    <label class=\"col-md-4\">Type the user ID to share your photo:</label>\n"
                            + "                    <div class=\"col-md-3\">\n"
                            + "                        <input type=\"text\" name=\"share_to1\" class=\"form-control\" required>\n"
                            + "                    </div>\n"
                            + "				</div>\n"
                            + "				<div class=\"row\" id=\"newRow\">\n"
                            + "                    <label class=\"col-md-5\"></label>\n"
                            + "                    <div class=\"col-md-3\">\n"
                            + "                        <input type=\"text\" name=\"share_to2\" class=\"form-control\" required>\n"
                            + "                    </div>\n"
                            + "				</div>\n"
                            + "				<div class=\"row\" id=\"newRow\">\n"
                            + "                    <label class=\"col-md-5\"></label>	\n"
                            + "                    <div class=\"col-md-3\">\n"
                            + "                        <input type=\"text\" name=\"share_to3\" class=\"form-control\" required>\n"
                            + "                    </div>\n"
                            + "				</div>\n"
                            + "				\n"
                            + "                    <label class=\"col-md-2\"></label>\n"
                            + "                \n"
                            + "                <div class=\"row p-1\"></div>\n"
                            + "                <hr>\n"
                            + "\n"
                            + "                <div class=\"row\">\n"
                            + "                    <img id=\"frame\" src=\"\" width=\"\" height=\"\" class=\"col-md-4\"/>\n"
                            + "                    \n"
                            + "                    <label class=\"col-md-4\"></label>\n"
                            + "                </div>\n"
                            + "                <div class=\"row p-1\"></div>\n"
                            + "\n"
                            + "                <div class=\"row\">\n"
                            + "                    <label class=\"col-md-3\"></label>\n"
                            + "                    <input type=\"submit\" value=\"Submit\" class=\"col-md-6 btn btn-primary btn-block\">\n"
                            + "                    <label class=\"col-md-3\"></label>\n"
                            + "                </div>\n"
                            + "            </form>\n"
                            + "\n"
                            + "    </body>\n"
                            + "</html>");
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(photoPermissions.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(photoPermissions.class.getName()).log(Level.SEVERE, null, ex);
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
