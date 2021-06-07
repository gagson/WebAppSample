/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class AdminDashboard extends HttpServlet {

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
                ServletContext application = getServletContext();
                SQLiteDataSource dataSource = (SQLiteDataSource) application.getAttribute("dataSource");
                try (Connection dbConn = dataSource.getConnection()) {
                    String manageQuery = "SELECT login, type, status FROM credential";
//                    ResultSet rs = dbConn.createStatement().executeQuery(manageQuery);
                    PreparedStatement manageStatement = dbConn.prepareStatement(manageQuery);
                    ResultSet rsManage = manageStatement.executeQuery();
                    out.println("<!DOCTYPE html>\n"
                            + "\n"
                            + "<html>\n"
                            + "    <head>\n"
                            + " <style>"
                            + "table, th, td {"
                            + "border: 1px solid black;}"
                            + "</style>"
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
                            + "                Admin Dashboard: Manage Users Page\n"
                            + "            </div>");
                    out.println("<div><h4><center>You have logged in as <b>" + login
                            + "</b></center></h4></div>");
                    out.println("        </div>\n"
                            + "            <form action=\"./receive\" method=\"post\" enctype=\"multipart/form-data\">\n"
                            + "                <div class=\"row p-1\"></div>\n");
//                            + "                    <div class=\"col-md-3\">\n"
//                            +"<div class=\"col-md-3\">\n");
                    while (rsManage.next()) {
                        String username = rsManage.getString(1);
                        String role = rsManage.getString(2);
                        String status = rsManage.getString(3);
                        out.println("<div class=\"row\" id=\"manageUser\">"
                                + "                    <label class=\"col-md-2\"></label>\n");
                        out.println("<div class=\"col-md-2\">\n");
                        out.println("<label class=\"col-md-1\">Username</label>\n");
                        out.println("<input type=\"text\" name=\"username\" class=\"form-control\" value=\"" + username + "\">");
                        out.println("</div>");
                        out.println("<div class=\"col-md-2\">\n");
                        out.println("<label class=\"col-md-1\">Role</label>\n");
                        out.println("<input type=\"text\" name=\"originalRole\" class=\"form-control\" value=\"" + role + "\">");
                        out.println("</div>");
                        out.println("<div class=\"col-md-2\">\n");
                        out.println("<label class=\"col-md-1\">Status</label>\n");
                        out.println("<input type=\"text\" name=\"originalStatus\" class=\"form-control\" value=\"" + status + "\">");
                        out.println("</div>\n");
//                        out.println("</div>");

                        out.println("<button type=\"submit\" value=\"" + username + "\" name=\"edit\" class=\"btn btn-outline-primary\">Edit Role</button>\n"
                                + "	<button type=\"submit\" value=\"" + username + "\"name=\"delete\" class=\"btn btn-outline-danger\">Delete</button>\n"
                                + "	<button type=\"submit\" value=\"" + username + "\" name=\"status\" class=\"btn btn-outline-dark\">Disable/Enable</button>\n"
                                + "                    <label class=\"col-md-2\"></label>\n"
                                + " </div></div>\n");
                    }

                    out.println("		<hr>\n"
                            + "				<div class=\"h6 text-center\">\n"
                            + "                Add New User\n"
                            + "				</div>\n"
                            + "				<div class=\"row p-1\"></div>\n"
                            + "                <div class=\"row\" id=\"newRow\">\n"
                            +"                    <label class=\"col-md-1\"></label>\n"
                            + "                    <label class=\"col-md-1\">Username:</label>\n"
                            + "                    <div class=\"col-md-3\">\n"
                            + "                        <input type=\"text\" name=\"addUsername\" class=\"form-control\">\n"
                            + "                    </div>\n"
                            + "					<label class=\"col-md-1\">Password:</label>\n"
                            + "					<div class=\"col-md-3\">\n"
                            + "                        <input type=\"password\" name=\"addPassword\" class=\"form-control\">\n"
                            + "                    </div>\n"
                            + "					<label>Role:</label>\n"
                            + "					<div class=\"col-md-1\">\n"
                            + "					<select name=\"addRole\">\n"
                            + "					<option value =\"ordinary\">Ordinary</option>\n"
                            + "					<option value =\"admin\">Admin</option>\n"
                            + "					</select> \n"
                            + "					</div>\n"
                            + "                    <label class=\"col-md-2\"></label>\n"
                            + "                </div>\n"
                            + "                <div class=\"row p-1\"></div>\n"
                            + "                <hr>\n"
                            + "\n"
                            + "               \n"
                            + "                <div class=\"row p-1\"></div>\n"
                            + "\n"
                            + "                <div class=\"row\">\n"
                            + "                    <label class=\"col-md-3\"></label>\n"
                            + "                    <input type=\"submit\" value=\"Submit\" onclick=\"form.action='./add_user';\" class=\"col-md-6 btn btn-primary btn-block\">\n"
                            + "                    <label class=\"col-md-3\"></label>\n"
                            + "                </div>\n"
                            + "            </form>\n");
                    out.println("<div class=\"row p-1\"></div>");
                    out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                    out.println("<button value=\"Logout\" name=\"logout\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"../logout\">Logout</a></div>"
                            + "    </body>\n"
                            + "</html>");
                }
            } else {
                response.sendRedirect("./login.jsp");
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
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
