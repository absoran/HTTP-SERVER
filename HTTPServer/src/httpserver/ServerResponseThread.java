package httpserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerResponseThread implements Runnable {

    FrameServer serverFrame;
    Socket connect;
    BufferedReader in = null;
    PrintWriter out = null;
    BufferedOutputStream dataOut = null;
    FileOutputStream fout = null;

    final int PORT = 8080;
    int downloadCount = 0;
    final File WEB_ROOT = new File(".");

    public ServerResponseThread(Socket c, FrameServer serverFrame) {
        this.connect = c;
        this.serverFrame = serverFrame;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            String fileRequested;
            String input = in.readLine();//gelen isteğin okunmayan kısmının ilk satırını oku
            serverFrame.addTextFrame("input: " + input);
            System.out.println("input: " + input); //örnek input= GET /index.html HTTP/1.1

            StringTokenizer parse = new StringTokenizer(input); // alınan satırı parse et // ilk satırı boşluklara göre ayırıyor //split etmek
            String method = parse.nextToken().toUpperCase(); // İstemcinin HTTP yöntemini alırız
            fileRequested = parse.nextToken().toLowerCase(); // talep edilen dosyayı öğreniriz
            String protocol = parse.nextToken(); //  HTTP/1.1
            String getlocation = System.getProperty("user.dir"); //server konumu / dosya yolu
            boolean isFile = fileRequested.length() > 1;
            if (method.equals("GET") && isFile) {
                File file = new File(getlocation + "\\" + fileRequested.substring(1)); //ilk karakter '/' olduğu için substring(1) yapıp 
                //kendi formatımızda dosya yolu oluşturuyoruz
                if (file == null) {
                    out.println(protocol + " 400 Bad Request");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println(); // üstbilgiler ve içerik arasındaki boş satır, çok önemli
                    dataOut.flush();
                } else {
                    int fileLength = (int) file.length();
                    String content = getContentType(fileRequested);
                    byte[] fileData = readFileData(file, fileLength);
                    // HTTP Headers gönderiyoruz ilkin
                    out.println(protocol + " 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println(); // üstbilgiler ve içerik arasındaki boş satır, çok önemli
                    out.flush(); // gönderirken ki akış tamponunu ( output stream buffer ) temizleme

                    dataOut.write(fileData, 0, fileLength); //dosyayı gönderiyoruz
                    dataOut.flush();
                }
            } else if (method.equals("PUT") && isFile) {
                File file = new File(getlocation + "\\" + fileRequested.substring(1)); //ilk karakter '/' olduğu için substring(1) yapıp 
                //kendi formatımızda dosya uolu oluşturuyoruz
                if (file == null) {
                    out.println(protocol + " 400 Bad Request");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println(); // üstbilgiler ve içerik arasındaki boş satır, çok önemli
                    dataOut.flush();
                } else {
                    int fileLength = (int) file.length();
                    String content = getContentType(fileRequested);
                    byte[] fileData = readFileData(file, fileLength);
                    // HTTP Headers gönderiyoruz ilkin
                    out.println(protocol + " 200 OK ");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println(); // üstbilgiler ve içerik arasındaki boş satır, çok önemli
                    out.flush(); // gönderirken ki akış tamponunu ( output stream buffer ) temizleme

                    dataOut.write(fileData, 0, fileLength); //dosyayı gönderiyoruz
                    dataOut.flush();
                }
            } else if (method.equals("DELETE") && isFile) {
                File file = new File(getlocation + "\\" + fileRequested.substring(1)); //ilk karakter '/' olduğu için substring(1) yapıp 
                //kendi formatımızda dosya uolu oluşturuyoruz
                if (file == null) {
                    out.println(protocol + " 400 Bad Request");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println(); // üstbilgiler ve içerik arasındaki boş satır, çok önemli
                    dataOut.flush();
                } else {
                    file.delete();
                }
            } else if (method.equals("POST") && isFile) {
                Post post = new Post((Socket) connect, in);
                post.processRequest();
            } else {
                out.println(protocol + " 404 Not Found ");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println();
                dataOut.flush();
            }

        } catch (IOException ex) {
            serverFrame.addTextFrame(ex.getMessage());
        } catch (Exception ex) {
            serverFrame.addTextFrame(ex.getMessage());
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                serverFrame.addTextFrame(e.getMessage());
            }
        }
    }

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
        return fileData;
    }
    public static void main(String[] args) {
        ArrayList<Server> list=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String str=list.get(i);
            
        }
        for(String str:list){
            
        }
    }

}
