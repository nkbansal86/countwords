/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package countwords.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nkbansal
 */
public class CountWords extends HttpServlet {


    protected String filePath = null;
    protected String delimeter;
    private Map<String, Integer> counts = null;

    /**
     *
     * @param servletConfig
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        this.filePath = servletConfig.getInitParameter("filePath");
        delimeter = servletConfig.getInitParameter("delimeter");
        this.delimeter = delimeter.substring(1, delimeter.length()-1);
        this.counts = new HashMap<String, Integer>();
        //this.delimeter = " ".getBytes(StandardCharsets.UTF_8)[0];
        loadData(this.filePath);
        //getServletContext().setAttribute("counts", counts);
    }
    private void loadData(String filePath) throws UnavailableException{
        File folder = new File(filePath);
        File[] listOfFiles;
        if (folder.isDirectory()){
            listOfFiles= folder.listFiles();
        } else {
            listOfFiles = new File[1];
            listOfFiles[0]=folder;
        }
        int count=0;
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (File file : listOfFiles) {
            
            if (file.isFile()) {
                System.out.println("Scanning the file:"+file.getPath());
                try {
                    scanFile(file);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CountWords.class.getName()).log(Level.SEVERE, null, ex);
                    throw new UnavailableException(this,"File not found:"+file.getAbsolutePath());
                }
            }
        }
    }
    private void scanFile(File file) throws FileNotFoundException, UnavailableException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        while(true){
            try {
                line = in.readLine();
            } catch (IOException ex) {
                throw new UnavailableException(this,"Exception occured while reading file:"+file.getAbsolutePath());
            }
            if (line==null){
                break;
            }
            String[] words = line.split(delimeter);
            for(int i=0; i<words.length; i++){
                String word = words[i].toLowerCase();
                if(counts.containsKey(word)){
                    counts.put(word, Integer.valueOf(counts.get(word)+1));
                    System.out.println("Increasing count for word '"+word+"'");
                }
                else{
                    counts.put(word, Integer.valueOf(1));
                    System.out.println("Adding new word '"+word+"'");
                }
            }
        }
    }
    private int getCount(String inputSearch) throws IOException{
        if(counts.containsKey(inputSearch)){
            return counts.get(inputSearch);
        }
        return 0;
    }
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
