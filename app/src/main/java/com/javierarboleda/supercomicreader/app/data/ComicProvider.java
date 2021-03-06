package com.javierarboleda.supercomicreader.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by javierarboleda on 5/23/16.
 *
 *
 */
public class ComicProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ComicDbHelper mComicDbHelper;

    static final int COMIC = 100;
    static final int CREATION = 200;
    static final int SAVED_PANEL = 300;

    @Override
    public boolean onCreate() {
        mComicDbHelper = new ComicDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case COMIC:
                retCursor = mComicDbHelper.getReadableDatabase().query(
                        ComicContract.ComicEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
           case CREATION:
                retCursor = mComicDbHelper.getReadableDatabase().query(
                        ComicContract.CreationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
           case SAVED_PANEL:
                retCursor = mComicDbHelper.getReadableDatabase().query(
                        ComicContract.SavedPanelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case COMIC:
                return ComicContract.ComicEntry.CONTENT_TYPE_DIR;
            case CREATION:
                return ComicContract.CreationEntry.CONTENT_TYPE_DIR;
            case SAVED_PANEL:
                return ComicContract.SavedPanelEntry.CONTENT_TYPE_DIR;
            default:
                throw new UnsupportedOperationException("Unknown ri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mComicDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case COMIC: {
                long _id = db.insert(ComicContract.ComicEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ComicContract.ComicEntry.buildComicUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CREATION: {
                long _id = db.insert(ComicContract.CreationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ComicContract.CreationEntry.buildCreationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SAVED_PANEL: {
                long _id = db.insert(ComicContract.SavedPanelEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ComicContract.SavedPanelEntry.buildSavedPanelUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mComicDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case COMIC:
                rowsDeleted = db.delete(
                        ComicContract.ComicEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CREATION:
                rowsDeleted = db.delete(
                        ComicContract.CreationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SAVED_PANEL:
                rowsDeleted = db.delete(
                        ComicContract.SavedPanelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mComicDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case COMIC:
                rowsUpdated = db.update(ComicContract.ComicEntry.TABLE_NAME, values, selection,
                        selectionArgs);
            case CREATION:
                rowsUpdated = db.update(ComicContract.CreationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
            case SAVED_PANEL:
                rowsUpdated = db.update(ComicContract.SavedPanelEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ComicContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ComicContract.PATH_COMIC, COMIC);
        matcher.addURI(authority, ComicContract.PATH_CREATION, CREATION);
        matcher.addURI(authority, ComicContract.PATH_SAVED_PANEL, SAVED_PANEL);

        return matcher;
    }
}
