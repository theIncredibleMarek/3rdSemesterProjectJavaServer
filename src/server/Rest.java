package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exceptions.NotFoundException;
import facades.Faffade;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Users;
import utils.Utils;

public class Rest
{

    private static int port;
    private static String ip;

    public Rest()
    {
        Properties properties = Utils.initProperties("server.properties");
        ip = properties.getProperty("serverIp");
        port = Integer.parseInt(properties.getProperty("serverPort"));
    }

    public void run() throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);

        //REST Routes
        server.createContext("/users", new HandlerUsers());

        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length >= 3)
        {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        new Rest().run();
    }

    class HandlerUsers implements HttpHandler
    {

        Faffade fafade = new Faffade();
        Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String response = "";
            int status = 200;
            String method = he.getRequestMethod().toUpperCase();
            switch (method)
            {
                case "GET":
                    try
                    {
                        String path = he.getRequestURI().getPath();
                        if (path.lastIndexOf("/") > 0)
                        { //users/blabla

                            System.out.println("no such command");
                        } else
                        { //users
                            //data in the headers
                            Headers headers = he.getRequestHeaders();
                            String username = headers.getFirst("username");
                            String password = headers.getFirst("password");
                            response = fafade.loginAsJSON(username, password);
                        }
                    } catch (NotFoundException ex)
                    {
                        response = "No person found for this id.";
                        status = 404;
                    }
                    break;
                case "POST":
                {
//                    InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
//                    BufferedReader br = new BufferedReader(isr);
//                    String jsonQuery = br.readLine();
//                    Users u = fafade.addUserFromGson(jsonQuery);
//                    response = gsonExpose.toJson(u);
//                    break;
                    Headers headers = he.getRequestHeaders();                    
                    Users u = new Users();
                    u.setUsername(headers.getFirst("username"));
                    u.setPassword(headers.getFirst("password"));
                    u.setType(headers.getFirst("userType"));
                    
                    Users addedUser = fafade.addUserFromGson(new Gson().toJson(u));
                    response = gsonExpose.toJson(addedUser);
                    break;
                }
                case "PUT":
                {
                    try
                    {
                        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String jsonQuery = br.readLine();
                        Users u = fafade.editUserFromGson(jsonQuery);
                        Gson gson1 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        response = gsonExpose.toJson(u);
                    } catch (NotFoundException ex)
                    {
                        response = "No person found for this id.";
                        status = 404;
                    }
                    break;
                }
                case "DELETE":
                    try
                    {
                        String path = he.getRequestURI().getPath();
                        int lastIndex = path.lastIndexOf("/");
                        if (lastIndex > 0)
                        {
                        //users/username
                            String username = path.substring(lastIndex + 1);
                            Users u = fafade.delete(username);

                            response = gsonExpose.toJson(u);
                        } else
                        {
                            status = 400;
                            response = "<h1>Bad Request</h1>No id supplied with request";
                        }
                    } catch (NumberFormatException nfe)
                    {
                        response = "Id is not a number";
                        status = 404;
                    } catch (NotFoundException ex)
                    {
                        Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
            he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            he.getResponseHeaders().add("Content-Type", "application/json");
            he.sendResponseHeaders(status, 0);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(response.getBytes());
            }
        }
    }
}
