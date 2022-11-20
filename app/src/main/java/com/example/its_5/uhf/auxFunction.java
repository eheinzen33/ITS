package com.example.its_5.uhf;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.example.its_5.DataClasses.ProductClass;
import com.example.its_5.DataClasses.TAG;
import com.example.its_5.SearchProductsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class auxFunction {
    private String user = "emaH";
    private String password = "Azure160296";
    private String sURL = "jdbc:mysql://p-grafico-database.mysql.database.azure.com:3306/rfid?useSSL=false";
    private static final String JSON_URL = "https://its-database-connection.azurewebsites.net/select_products.php";
    private static HashMap<String,String> productsByEPC = new HashMap<>();
    List<ProductClass> productList;
    private Connection connection;

    public String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String convertStringToHex(String str) {

        StringBuffer hex = new StringBuffer();

        // loop chars one by one
        for (char temp : str.toCharArray()) {

            // convert char to int, for char `a` decimal 97
            int decimal = (int) temp;

            // convert int to hex, for decimal 97 hex 61
            hex.append(Integer.toHexString(decimal));
        }

        return hex.toString();

    }


    public Void getProductsList() throws SQLException {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        connection = DriverManager.getConnection(sURL, user, password);
//        String query = "SELECT epc, product_name FROM client_products;";
//        try (Statement stmt = connection.createStatement()) {
//            ResultSet rs = stmt.executeQuery(query);
//            while (rs.next()) {
//                productsByEPC.put(rs.getString("epc"),rs.getString("product_name"));
//            }
//        } catch (SQLException e) {
//            System.out.println(e);
//        }
        GetData getData = new GetData();
        getData.execute();
        return null;
    }

    public ArrayList<TAG> get_dif_products(List<Map<String, Object>> EPCReadedList, String stage){
        Map<String,Integer> QtyByEPC = new HashMap<>();
        ArrayList<TAG> productsQty = new ArrayList<>();
        for (Map<String, Object> map : EPCReadedList) {
            String epcShort = ((String) map.get("Product")).substring(0,6);
            if (!productsByEPC.containsKey(epcShort)){
                epcShort = convertStringToHex((String) map.get("Product"));}
            if (!QtyByEPC.containsKey(epcShort)){
                QtyByEPC.put(epcShort, 1);
            }else if(QtyByEPC.containsKey(epcShort)) {
                Integer newQty = QtyByEPC.get(epcShort) + 1;
                QtyByEPC.put(epcShort,newQty);
            }
        }
        for (String key : QtyByEPC.keySet()) {
            Integer qty = (QtyByEPC.get(key));
            String product;
//            if (productsByEPC.get(key.substring(0,6)) == null){epcAux = convertStringToHex(key);}else {epcAux = productsByEPC.get(key.substring(0,6));}
            product = productsByEPC.getOrDefault(key, key.toUpperCase(Locale.ROOT));
            TAG tag = new TAG(Date.valueOf(String.valueOf(LocalDate.now())), key, product, qty,stage);
            productsQty.add(tag);
        }
        return productsQty;
    }

    public void toMySQL(ArrayList<TAG> TagListToUpload){
        System.out.println("Database connected...ready to extract information.");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            connection = DriverManager.getConnection(sURL, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "INSERT INTO raw_scan_data ("
                + "date,"
                + "epc,"
                + "product,"
                + "quantity,"
                + "stage ) VALUES ("
                + "?, ?, ?, ?, ?)";
        for (TAG tag : TagListToUpload) {

            try (PreparedStatement st = connection.prepareStatement(query)) {
                st.setDate(1, tag.getDate());
                st.setString(2, tag.getEPC()); //epc es el producto en el List Map rt2 que aca se llama tags
                st.setString(3, tag.getProduct());
                st.setInt(4, tag.getQuantity());
                st.setString(5, tag.getStage());
                st.executeUpdate();
                System.out.println("Information uploaded.");
            } catch (SQLException se){
                System.out.println(se.getMessage());
            }
        }

    }

    public static class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();


                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data !=-1){
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0 ; i< jsonArray.length() ; i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    productsByEPC.put(jsonObject1.getString("epc"),jsonObject1.getString("product_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
