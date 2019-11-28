package httpserver;

import java.io.*;
import java.net.*;

public class Post implements Runnable {

    final static int BUF_SIZE = 1024000000;
    final static String CRLF = "\r\n";

    byte[] buffer;
    Socket socket;
    BufferedReader br;
    String filename = "";

    public Post(Socket socket, BufferedReader br) throws Exception {
        this.socket = socket;
        this.br = br;
        buffer = new byte[BUF_SIZE];
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getContent() throws Exception {
        int total = 0, rcv = 0;
        boolean finish = false;
        System.out.println("while 3");
        while (rcv >= 0) {
            rcv = socket.getInputStream().read(buffer, total,
                    BUF_SIZE - total - 1);
            String msg = new String(buffer, total, rcv);
            System.out.println("msg: " + msg);
            total += rcv;
            if (msg.indexOf("filename") != -1) {
                for(int i=msg.indexOf("filename") + 10;i<msg.length();i++){
                    if(msg.charAt(i)=='\t' || msg.charAt(i)=='\n'){
                        break;
                    }
                    filename+=msg.charAt(i);
                }
                System.out.println("file namememem: " + filename);
            }
            if (msg.indexOf("Upload") != -1 || (msg.indexOf("--WebKitFormBoundary") != -1 && finish)) {
                System.out.println("EXITING");
                break;
            }
            if (msg.indexOf("--WebKitFormBoundary") != -1) {
                finish = true;
            }
            // Only loop if it is not a GET message and have not reached
            // end of POST message, Upload+CRLF represents end of request

        }
        // returns the total bytes in the buffer
        return total;
    }

    public void processRequest() throws Exception {
        int total = getContent();

        // InputStream is = socket.getInputStream();
//        InputStream is = new ByteArrayInputStream(buffer, 0, total);
//        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        // BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        BufferedReader br = new BufferedReader(new InputStreamReader(is,
//                "US-ASCII"));
//        String requestLine = br.readLine();
        // Extract the filename from the request line.
//        StringTokenizer tokens = new StringTokenizer(requestLine);
//        String method = "POST";//tokens.nextToken(); // skip over the method, which
        // should be "GET"
        String newfilename = br.readLine();
        if (true /*|| method.indexOf("POST") == 0*/) {
            String content = new String(buffer, 0, total, "US-ASCII");
            if (filename.length() > 3) {
                newfilename = filename;
            } else {
                System.out.println("while 1");
                while (-1 == newfilename.indexOf("-WebKitFormBoundary")) {
                    newfilename = br.readLine();
                }
                for (int i = 0; i < 2; i++) {
                    newfilename = br.readLine();
                }
                System.out.println("while 2");
                while (-1 == newfilename.indexOf("--WebKitFormBoundary")) {
                    System.out.println("" + newfilename);
                    newfilename = br.readLine();
                }
                for (int i = 0; i < 3; i++) {
                    newfilename = br.readLine();
                }
                System.out.println("Create new file: " + newfilename);
            }

            int begin = content.indexOf("------WebKitFormBoundary", 0);
            for (int i = 0; i < 4; i++) {
                begin = content.indexOf(CRLF, begin + 1);
            }

            begin += 2;
            int end = content.indexOf("------WebKitFormBoundary", begin);
            int length = end - begin;
            System.out.println("FILESIZE: " + length);

            String loca = System.getProperty("user.dir");
            System.out.println("file " + loca + "\\" + newfilename);
            String path = loca + "\\" + newfilename;
            String path2 = "";
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c==':' || c=='.' || c == '\\' || ((int) c >= 65 && (int) c <= 90) || ((int) c >= 97 && (int) c <= 122) || ((int) c >= 48 && (int) c <= 57)) {
                    path2+=(char)c;
                }
            }
            System.out.println("path2:  "+path2);
            File file = new File( path2);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(file);
            DataOutputStream dout = new DataOutputStream(fout);

            dout.write(buffer, begin, length);

            dout.close();
//        br.close();
//        socket.close();
        }

//        String fileName = tokens.nextToken();
//
//        // Prepend a "." so that file request is within the current directory.
//        fileName = "." + fileName;
//
//        // Open the requested file.
//        FileInputStream fis = null;
//        boolean fileExists = true;
//        try {
//            fis = new FileInputStream(fileName);
//        } catch (FileNotFoundException e) {
//            fileExists = false;
//        }
        // Construct the response message.
//        String statusLine = null;
//        String contentTypeLine = null;
//        String entityBody = null;
////        if (fileExists) {
//        statusLine = "HTTP/1.0 200 OK" + CRLF;
//        contentTypeLine = "Content-Type: " + contentType(newfilename) + CRLF;
//        } else {
//            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
//            contentTypeLine = "Content-Type: text/html" + CRLF;
//            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
//                    + "<BODY>Not Found</BODY></HTML>";
//        }
        // Send the status line.
//        os.writeBytes(statusLine);
//
//        // Send the content type line.
//        os.writeBytes(contentTypeLine);
//
//        // Send a blank line to indicate the end of the header lines.
//        os.writeBytes(CRLF);
//
//        // Send the entity body.
////        if (fileExists) {
////            sendBytes(fis, os);
////            fis.close();
////        } else {
////            os.writeBytes(entityBody);
////        }
//        // Close streams and socket.
//        os.close();
//        br.close();
//        socket.close();
    }

    private static void sendBytes(FileInputStream is, OutputStream os)
            throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while ((bytes = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (fileName.endsWith(".zip")) {
            return "application/zip";
        }
        if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    public static void main(String argv[]) throws Exception {
        // Get the port number from the command line.
        int port = 6789;

        // Establish the listen socket.
        ServerSocket socket = new ServerSocket(port);

        // Process HTTP service requests in an infinite loop.
        while (true) {
            // Listen for a TCP connection request.
            Socket connection = socket.accept();

            // Construct an object to process the HTTP request message.
//            Post request = new Post(connection);
            // Create a new thread to process the request.
//            Thread thread = new Thread(request);
            // Start the thread.
//            thread.start();
        }
    }
}
