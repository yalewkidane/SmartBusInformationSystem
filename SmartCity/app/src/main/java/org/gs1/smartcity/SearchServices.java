package org.gs1.smartcity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SearchServices extends AppCompatActivity {

    private static final String REQUEST_URL = "http://143.248.55.137:8080/smartcity/getServiceList?id=";
    private Handler handler = new Handler();
    private List<ServiceType> serviceList = new ArrayList<ServiceType>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_search);

        String objectID = null;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            objectID = extras.getString("objectID");
        }
        if (objectID.length() > 0) {
            getServiceList(objectID);
        } else {
            Toast.makeText(getApplicationContext(), "Please input object ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            synchronized (serviceList) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String serviceUrl = serviceList.get(0).getServiceUrl();
        serviceUrl = serviceUrl + objectID;
        serviceList.get(0).setServiceUrl(serviceUrl);
        String serviceName = serviceList.get(0).getServiceName();
        serviceName = serviceName.replaceAll("_", " ");
        serviceName = Character.toUpperCase(serviceName.charAt(0)) + serviceName.substring(1);

        TextView textView = (TextView) findViewById(R.id.service_name01);
        textView.setText(serviceName);

        TextView textView1 = (TextView) findViewById(R.id.service_url01);
        textView1.setText(serviceList.get(0).getServiceUrl());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getServiceList(String objectID) {

        ConnectThread thread = new ConnectThread(objectID);
        thread.start();
    }

    public void onGoClicked(View v) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceList.get(0).getServiceUrl()));
        startActivity(intent);
    }

    private class ConnectThread extends Thread {

        String objectID;

        public ConnectThread(String objectID) {
            this.objectID = objectID;
        }
        public void run() {
            String output = getServiceString(objectID);
            serviceList.addAll(getServiceList(output));
            synchronized (serviceList) {
                serviceList.notify();
            }
        }

        private String getServiceString(String objectID) {

            StringBuilder output = new StringBuilder();
            String url = REQUEST_URL + objectID;
            try {
                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }
                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return output.toString();
        }

        private List<ServiceType> getServiceList(String data) {

            List<ServiceType> list = new ArrayList<ServiceType>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document document = null;
            try {
                document = builder.parse(new InputSource(new StringReader(data)));
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeList nList = document.getElementsByTagName("Service");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                Element element = (Element) nNode;

                ServiceType service = new ServiceType();
                service.setServiceName(element.getElementsByTagName("serviceName").item(0).getFirstChild().getNodeValue());
                service.setServiceUrl(element.getElementsByTagName("serviceUrl").item(0).getFirstChild().getNodeValue());
                list.add(service);
            }
            return list;
        }
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SearchServices Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
