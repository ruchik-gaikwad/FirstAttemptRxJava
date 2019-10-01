package io.stackroute;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.stackroute.Model.GitUserRepos;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


public class GenericFunctions {
    // This was an attempt to write a custom Emmit Handler
    // which can emmit a value every 2 secs just trying
    // to build something async
   public static final Emitter emmitHnadler = new Emitter() {
       @Override
       public void onNext(Object o) {
           System.out.println("Got Next ");
           System.out.println(o);
       }

       @Override
       public void onError(Throwable throwable) {
            System.out.println("Got Error");
            System.out.println(throwable);
       }

       @Override
       public void onComplete() {
            System.out.println("Got Completed");
       }
   };

   // This is a Consumer Function can be passed to the subscriber
   public static final Consumer a = e -> {
     System.out.println(e + " is this going To work ??");
   };

   // this is a function can be passed to any operator
   public static final Function AddThreeEnachValue = (e) ->  {
       int number = (int) e;
       number = number + 3;
       return number;
   };

   // This is used to  Map the String to POJO produced by the observable
   public static final Function ConvertStringToPOJO = (e) -> {
       String data = (String) e;
       ObjectMapper objectMapper = new ObjectMapper();
       objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
       objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
       GitUserRepos[] repos = objectMapper.readValue(data, GitUserRepos[].class);;
       return repos;
   };

   // This makes async api call to GitHub using the Aapche Http Client
   // The idea is to spawn a new thread do the operations there
   // and then return it to the main thread
   public static final ObservableOnSubscribe RequestObservable = e -> {
       // Spawn a new Thread
       Thread asyncRequestThread = new Thread(()-> {

            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.

            String url_apacheClient = "http://api.github.com/users/ruchik-gaikwad/repos";
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url_apacheClient);

            try {

                CloseableHttpResponse response = httpclient.execute(httpGet);
                HttpEntity  dataEntity = response.getEntity();

                BufferedReader reader = new BufferedReader(new InputStreamReader(dataEntity.getContent()));
                String output = reader.readLine();
                response.close();
                e.onNext(output);

            } catch (MalformedURLException ex) {
                e.onError(new MalformedURLException("Incorrect Url"));
                ex.printStackTrace();
            } catch (IOException ex) {
                e.onError(new IOException("Could'nt Make the Call"));
                ex.printStackTrace();
            }

        });
          asyncRequestThread.start();

   };
}
