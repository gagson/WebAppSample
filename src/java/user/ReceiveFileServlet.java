/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author gagso
 */
public class ReceiveFileServlet extends HttpServlet {

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
    public static byte[] readBytesAndClose(InputStream in) throws IOException {
    try {
        int block = 4 * 1024;
        ByteArrayOutputStream out = new ByteArrayOutputStream(block);
        byte[] buff = new byte[block];
        while (true) {
            int len = in.read(buff, 0, block);
            if (len < 0) {
                break;
            }
            out.write(buff, 0, len);
        }
        return out.toByteArray();
    } finally {
        in.close();
    }
}
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        String type = (String) session.getAttribute("type");
        try (PrintWriter out = response.getWriter()) {
            if ((login != null) && (type != null)) {
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            InputStream image = filePart.getInputStream();
            String kw1 = request.getParameter("kw1");
            String kw2 = request.getParameter("kw2");
            String kw3 = request.getParameter("kw3");
//            ServletContext application = getServletContext();
//            ArrayList<String> kwList = (ArrayList<String>) application.getAttribute("keyword");
//            File destinationFolder = new File(homeFolder);
//            if (!destinationFolder.exists()) {
//                destinationFolder.mkdirs();
//            }
//            filePart.write(destinationFolder.getAbsolutePath() + File.separator + fileName);
            SQLiteDataSource dataSource = (SQLiteDataSource) getServletContext().getAttribute("dataSource");
            if (dataSource != null) {
                try (Connection dbConn = dataSource.getConnection()) {
                    String insertString = "INSERT INTO photos (user_login, file_name, keyword1, keyword2, keyword3, image_data) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = dbConn.prepareStatement(insertString)) {
                        insertStatement.setString(1, login);
                        insertStatement.setString(2, fileName);
                        insertStatement.setString(3, kw1);
                        insertStatement.setString(4, kw2);
                        insertStatement.setString(5, kw3);
			insertStatement.setBytes(6, readBytesAndClose(image));
                        insertStatement.executeUpdate();
                    }
                }
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>File Storage</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Success!!</h1>");
                out.println("<a href=\"dashboard\">Go back to Dashboard</a>");
                out.println("</body>");
                out.println("</html>");
                
            } else {
                response.sendRedirect(request.getContextPath());
            }
        }   else {
                response.sendRedirect(request.getContextPath());
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
        response.sendRedirect("dashboard");
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
            Logger.getLogger(ReceiveFileServlet.class.getName()).log(Level.SEVERE, null, ex);
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
