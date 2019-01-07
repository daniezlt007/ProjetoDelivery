package br.com.projetodeliverytcc.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.projetodeliverytcc.model.Order;

/**
 * Created by daniel on 28/02/18.
 */

public class Database extends SQLiteAssetHelper {

    private static final String DBNAME="ComprexDB.db";
    private static final int VERSION = 1;

    public Database(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public List<Order> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb =  new SQLiteQueryBuilder();
        String[] sql = {"ID","ProductName","ProductId","Quantity","Price","Discount"};
        String sqlTable = "OrderDetail";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sql, null, null, null,null, null);
        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(
                c.getInt(c.getColumnIndex("ID")),
                c.getString(c.getColumnIndex("ProductId")),
                c.getString(c.getColumnIndex("ProductName")),
                c.getString(c.getColumnIndex("Quantity")),
                c.getString(c.getColumnIndex("Price")),
                c.getString(c.getColumnIndex("Discount"))));
            }while(c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("insert into OrderDetail(ProductId, ProductName, Quantity, Price, Discount)" +
                "values ('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("delete from OrderDetail");
        db.execSQL(query);
    }

    public void addToFavorites(String productId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("insert into Favoritos (ProductId) values ('%s');", productId);
        db.execSQL(query);
    }

    public void removeFromFavorites(String productId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("delete from Favoritos where ProductId='%s';", productId);
        db.execSQL(query);
    }

    public boolean isFavorites(String productId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("select * from Favoritos where ProductId='%s';", productId);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCard() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("select count(*) from OrderDetail");
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                count = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail set Quantity=%s where ID=%d", order.getQuantity(), order.getID());
        db.execSQL(query);
    }
}
