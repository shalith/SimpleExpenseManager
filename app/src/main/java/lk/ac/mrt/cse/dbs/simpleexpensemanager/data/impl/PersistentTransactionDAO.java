/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private final List<Transaction> transactions;

    public static final String DATABASE_NAME = "130155G.db";
    public static final String TRANSACTIONS_TABLE_NAME = "transactions";
    public static final String TRANSACTIONS_COLUMN_ID = "id";
    public static final String TRANSACTIONS_COLUMN_DATE = "date";
    public static final String TRANSACTIONS_COLUMN_ACCOUNT_NO = "accountNo";
    public static final String TRANSACTIONS_COLUMN_EXPENSE_TYPE = "expenseType";
    public static final String TRANSACTIONS_COLUMN_AMOUNT = "amount";

    public PersistentTransactionDAO(Context context) {
        super(context, DATABASE_NAME , null, 1);
        transactions = new LinkedList<>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", String.valueOf(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", String.valueOf(expenseType));
        contentValues.put("amount", amount);
        db.insert("transactions", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transactions", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Transaction transaction = new Transaction(java.sql.Date.valueOf(res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_DATE)))
                    ,res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_ACCOUNT_NO))
                    ,ExpenseType.valueOf(res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_EXPENSE_TYPE)))
                    ,Double.parseDouble(res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_AMOUNT))));
            array_list.add(transaction);
            res.moveToNext();
        }
        return array_list;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = getAllTransactionLogs().size();
        if (size <= limit) {
            return getAllTransactionLogs();
        }

        return getAllTransactionLogs().subList(size - limit, size);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table transactions " +
                        "(id integer primary key AUTOINCREMENT, date text,accountNo text,expenseType text, balance real)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);

    }
}
