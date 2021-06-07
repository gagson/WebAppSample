/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author gagso
 */
public class photoSearch extends HttpServlet {

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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        String homeFolder = (String) session.getAttribute("homeFolder");
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null) && (homeFolder != null)) { //logged in version
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
                        + "            <h2>Photo Search Page</h2>\n"
                        + "        </nav>\n"
                        + "        <div class=\"container-fluid\">\n"
                        + "            <div class=\"h5 text-center\">\n"
                        + "                Search photos with keywords\n"
                        + "            </div>\n");
                out.println("<div><center><h6>You have logged in as <b>" + login
                            + "</b></center></h6></div>"                        
                        + "            <form action=\"search_results\" method=\"post\" enctype=\"multipart/form-data\">\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "                <div class=\"row\" id=\"newRow1\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 1:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"keyword1\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "<div class=\"row p-1\"></div>"
                        + "				<div class=\"row\" id=\"newRow2\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 2:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"keyword2\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "<div class=\"row p-1\"></div>"
                        + "				<div class=\"row\" id=\"newRow3\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 3:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"keyword3\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "                <hr>\n"
                        + "\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "\n"
                        + "                <div class=\"row\">\n"
                        + "                    <label class=\"col-md-3\"></label>\n"
                        + "                    <input type=\"submit\" value=\"Submit\" class=\"col-md-6 btn btn-primary btn-block\">\n"
                        + "                    <label class=\"col-md-3\"></label>\n"
                        + "                </div>\n"
                        + "            </form>\n");
                out.println("<div class=\"row p-1\"></div>");
                out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                out.println("<button value=\"dashboard\" name=\"dashboard\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"user/dashboard\">Go back to dashboard</a></div>");
                out.println("<div class=\"row p-1\"></div>");
                out.println("<div class=\"row\"><label class=\"col-md-8\"></label>");
                out.println("<button value=\"Logout\" name=\"logout\" class=\"col-md-2 btn btn-light btn-block\"><a href=\"./logout\">Logout</a></div>"
                        + "    </body>\n"
                        + "</html>");
            } else { //public
                out.println("<!DOCTYPE html>\n"
                        + "\n"
                        + "<html>\n"
                        + "    <head>\n"
                        + "        <title>Photo Repository</title>\n"
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
                        + "            <h2>Photo Search Page</h2>\n"
                        + "        </nav>\n"
                        + "        <div class=\"container-fluid\">\n"
                        + "            <div class=\"h5 text-center\">\n"
                        + "                Search photos with keywords\n"
                        + "            </div>\n"
                        + "<div><h4><center>You are in <b>Public</b> page</center>"
                        + "Click <a href=\"./login.jsp\">Here</a> if you want to log in</h4></div>"
                        + "            <form action=\"search_results\" method=\"post\" enctype=\"multipart/form-data\">\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "                <div class=\"row\" id=\"newRow1\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 1:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"kw1\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "				<div class=\"row\" id=\"newRow2\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 2:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"kw2\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "				<div class=\"row\" id=\"newRow3\">\n"
                        + "                    <label class=\"col-md-4\"></label>\n"
                        + "                    <label class=\"col-md-2\">Keyword 3:</label>\n"
                        + "                    <div class=\"col-md-3\">\n"
                        + "                        <input type=\"text\" name=\"kw3\" class=\"form-control\">\n"
                        + "                    </div>\n"
                        + "                    <label class=\"col-md-2\"></label>\n"
                        + "                </div>\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "                <hr>\n"
                        + "\n"
                        + "                <div class=\"row p-1\"></div>\n"
                        + "\n"
                        + "                <div class=\"row\">\n"
                        + "                    <label class=\"col-md-3\"></label>\n"
                        + "                    <input type=\"submit\" value=\"Submit\" class=\"col-md-6 btn btn-primary btn-block\">\n"
                        + "                    <label class=\"col-md-3\"></label>\n"
                        + "                </div>\n"
                        + "            </form>\n"
                        + "    </body>\n"
                        + "</html>");
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
        processRequest(request, response);
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
        processRequest(request, response);
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
