/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package countwords.src;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nkbansal
 */
public class CountWords extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected String filePath = null;
    protected byte delimeter;
    private static final int MAPSIZE = 4 * 1024 ;
    public void init(ServletConfig servletConfig) throws ServletException{
        this.filePath = servletConfig.getInitParameter("filePath");
        this.delimeter = servletConfig.getInitParameter("delimeter").getBytes(StandardCharsets.UTF_8)[1];
        //this.delimeter = " ".getBytes(StandardCharsets.UTF_8)[0];
    }
    private int getCount(String inputSearch) throws IOException{
        String paddedInput = delimeter + inputSearch + delimeter;
        String paddedInputStart = inputSearch + delimeter;
        String paddedInputEnd = delimeter +inputSearch ;
        File folder = new File(filePath);
        File[] listOfFiles;
        if (folder.isDirectory()){
            listOfFiles= folder.listFiles();
        } else {
            listOfFiles = new File[1];
            listOfFiles[0]=folder;
        }
        int count=0;
        for (File file : listOfFiles) {
            
            if (file.isFile()) {
                System.out.println("Searching in file:"+file.getPath());
                count+=searchFor(Paths.get(file.getPath()), inputSearch);
            }
        }
        return count;
    }
    private int searchFor(Path path, String grepFor) throws IOException{
        final byte[] tosearch = grepFor.getBytes(StandardCharsets.UTF_8);
        //StringBuilder report = new StringBuilder();
        int padding = 1; // need to scan 1 character ahead in case it is a word boundary.
        //int linecount = 0;
        int matches = 0;
        boolean inword = false;
        boolean scantolineend = false;
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)){
            final long length = channel.size();
            int pos = 0;
            while(pos < length) {
                long remaining = length - pos;
                // int conversion is safe because of a safe MAPSIZE.. Assume a reaosnably sized tosearch.
                int trymap = MAPSIZE + tosearch.length + padding;
                int tomap = (int)Math.min(trymap, remaining);
                // different limits depending on whether we are the last mapped segment.
                int limit = trymap == tomap ? MAPSIZE : (tomap - tosearch.length);
                MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, pos, tomap);
                //System.out.println("Mapped from " + pos + " for " + tomap);
                pos += (trymap == tomap) ? MAPSIZE : tomap;
                for (int i = 0; i < limit; i++) {
                    final byte b = buffer.get(i);
                    if (b == '\r' || b=='\n' || b == delimeter) {
                        inword = false;
                    } else if (!inword) {
                        if (findMatch(buffer, i, tomap, tosearch)) {
                            matches++;
                            i += tosearch.length - 1;
                        } else {
                            inword = true;
                        }
                    }
                }
            }
            channel.close();
        } 
        return matches;
    }
    private boolean findMatch(MappedByteBuffer buffer, int pos, int tomap, byte[] tosearch) {
        String temp = new String(tosearch, StandardCharsets.UTF_8);
        temp = temp.toUpperCase();
        byte[] toSearchUpper = temp.getBytes(StandardCharsets.UTF_8);
        //assume at valid word start.
        for (int i = 0; i < tosearch.length; i++) {
            byte b = buffer.get(pos+i);
            System.out.print(String.format("b:%s, tosearch:%s, toSearchUpper: %s", b, tosearch[i], toSearchUpper[i]));
            if (tosearch[i] != b && toSearchUpper[i] != b) {
                return false;
            }
        }
        byte nxt = (pos + tosearch.length) == tomap ? (byte)delimeter : buffer.get(pos + tosearch.length); 
        System.out.println("nxt:"+nxt+", delimeter:"+(byte)delimeter);
        boolean result= nxt == delimeter || nxt == '\n' || nxt == '\r';
        System.out.println("Result:"+result);
        return result;
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        //Enumeration params = request.getParameterNames();
        Map params = request.getParameterMap();
        String inputSearch;
        int count=-1;
        if (params.containsKey("query")){
            inputSearch = request.getParameter("query");
            inputSearch = inputSearch.toLowerCase();
            count = getCount(inputSearch);
        }
               
        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print(String.format("\"count\": %d", count));
            //out.println(String.format("\"filePath\":%s", filePath));
            //out.println(String.format("\"delimeter\":\"%s\"", delimeter));
            out.print("}");
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
