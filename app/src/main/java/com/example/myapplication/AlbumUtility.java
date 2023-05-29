package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlbumUtility {
    private SharedPreferences sharedPreferences;
    private static AlbumUtility instance;
    private static final String ALL_ALBUM_KEY = "album_list";
    private static final String ALL_ALBUM_DATA_KEY = "album_data";
    private List<Album> listAlbum;
    private List<MyImage> listImage;
    public Context context;


    private AlbumUtility(Context context) {
        sharedPreferences = context.getSharedPreferences("albums_database", Context.MODE_PRIVATE);

        if (getAllAlbums() == null) {
            initAlbums();
        }
        if (getAllAlbumData() == null) {
            initAlbumData();
        }


    }


    //list
    @NonNull
    public List<Album> getListAlbum(List<MyImage> listImage) {
        List<String> ref = new ArrayList<>();
        List<Album> listAlbum = new ArrayList<>();

        for (int i = 0; i < listImage.size(); i++) {
            String[] _array = listImage.get(i).getThumb().split("/");
            String _pathFolder = listImage.get(i).getThumb().substring(0, listImage.get(i).getThumb().lastIndexOf("/"));
            String _name = _array[_array.length - 2];
            if (!ref.contains(_pathFolder)) {
                ref.add(_pathFolder);
                Album token = new Album(listImage.get(i), _name);
                token.setPathFolder(_pathFolder);
                token.addItem(listImage.get(i));
                listAlbum.add(token);
            } else {
                listAlbum.get(ref.indexOf(_pathFolder)).addItem(listImage.get(i));
            }
        }
        return listAlbum;
    }//Get List Album


    public static AlbumUtility getInstance(Context context) {
        if (null == instance)
            instance = new AlbumUtility(context);
        return instance;
    }

    public ArrayList<String> getAllAlbums() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(ALL_ALBUM_KEY, null), type);
    }

    public ArrayList<AlbumData> getAllAlbumData() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AlbumData>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(ALL_ALBUM_DATA_KEY, null), type);
    }

    public void setAllAlbums(ArrayList<String> albums) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ALL_ALBUM_KEY);
        editor.putString(ALL_ALBUM_KEY, gson.toJson(albums));
        editor.apply();
    }

    public void setAllAlbumData(ArrayList<AlbumData> data) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ALL_ALBUM_DATA_KEY);
        editor.putString(ALL_ALBUM_DATA_KEY, gson.toJson(data));
        editor.apply();
    }

    public void editAlbumName(String oldName, String newName) {
        ArrayList<AlbumData> data = getAllAlbumData();
        if (data != null) {
            for (AlbumData d: data) {
                if (d.getAlbumName().equals(oldName))
                    d.setAlbumName(newName);
            }
            setAllAlbumData(data);
        }
    }

    private void initAlbums() {
        ArrayList<String> albums = new ArrayList<String>();
        listAlbum = getListAlbum(listImage);
        albums.add(  String.valueOf(listAlbum.indexOf(0)));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ALBUM_KEY, gson.toJson(albums));
        editor.apply();
    }

    private void initAlbumData() {
        ArrayList<AlbumData> albumData = new ArrayList<AlbumData>();
        listImage = GetAllPhotoFromGallery.getAllImageFromGallery(context);
        albumData.add(new AlbumData(String.valueOf(listAlbum.indexOf(0)), new ArrayList<String>()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ALBUM_DATA_KEY, gson.toJson(albumData));
        editor.apply();
    }

    public boolean addNewAlbum(String albumName) {
        ArrayList<String> albums = getAllAlbums();
        ArrayList<AlbumData> data = getAllAlbumData();
        if (albums != null && data != null)
            if (albums.add(albumName) && data.add(new AlbumData(albumName, new ArrayList<String>()))) {
                setAllAlbums(albums);
                setAllAlbumData(data);
                return true;
            }
        return false;
    }

    public boolean addPictureToAlbum(String albumName, String picturePath) {
        ArrayList<AlbumData> data = getAllAlbumData();
        if (null != data) {
            AlbumData selectedAlbum = findDataByAlbumName(albumName);
            if (selectedAlbum != null) {
                if (selectedAlbum.addNewPath(picturePath)){
                    //TODO:
                    //data.removeIf(d -> d.getAlbumName().equals(selectedAlbum.getAlbumName()));
                    Iterator<AlbumData> iter = data.iterator();
                    while (iter.hasNext()) {
                        AlbumData adata = iter.next();
                        if (adata.getAlbumName().equals(selectedAlbum.getAlbumName())) {
                           iter.remove();
                        }
                    }
                    data.add(selectedAlbum);
                }
            }
            setAllAlbumData(data);
            return true;
        }
        return false;
    }

    public boolean addToTrashed(String picturePath) {
        String currentFilename = picturePath.substring(picturePath.lastIndexOf('/') + 1);
        String newFilename = ".trashed" + currentFilename;

        File directory = new File(picturePath.substring(0, picturePath.lastIndexOf('/')));
        File from = new File(directory, currentFilename);
        File to = new File(directory, newFilename);

        deletePictureInAllAlbums(from.getAbsolutePath());
        if (from.renameTo(to)) {
            addPictureToAlbum("Trashed", to.getAbsolutePath());
            return true;
        }
        return false;
    }

    public boolean recoverFromTrashed(String picturePath) {
        String oldFilename = picturePath.substring(picturePath.lastIndexOf('/') + 1);
        String newFilename = oldFilename.replace(".trashed", "");

        File directory = new File(picturePath.substring(0, picturePath.lastIndexOf('/')));
        File from = new File(directory, oldFilename);
        File to = new File(directory, newFilename);
        deletePictureInAlbum("Trashed", from.getAbsolutePath());

        return from.renameTo(to);
    }

    public boolean deleteAlbum(String albumName) {
        ArrayList<String> albums = getAllAlbums();
        ArrayList<AlbumData> data = getAllAlbumData();
        if (albums != null && data != null)
            for (String album: albums)
                if (album.equals(albumName))
                    if (albums.remove(album)) {
                        //TODO:
                        //data.removeIf(d->d.getAlbumName().equals(albumName));
                        Iterator<AlbumData> iter = data.iterator();
                        while (iter.hasNext()) {
                            AlbumData element = iter.next();
                            if (element.getAlbumName().equals(albumName)) {
                                iter.remove();
                            }
                        }
                        setAllAlbums(albums);
                        setAllAlbumData(data);
                        return true;
                    }
        return false;
    }

    public boolean deletePictureInAlbum(String albumName, String picturePath) {
        // Get all album data
        ArrayList<AlbumData> data = getAllAlbumData();
        // Get AlbumData object matching the name
        AlbumData albumData = findDataByAlbumName(albumName);
        if (albumData != null) {


            // Remove required path in AlbumData object
            ArrayList<String> paths = albumData.getPicturePaths();
            //TODO:
            //paths.removeIf(s -> s.equals(picturePath));
            Iterator<String> iterPath = paths.iterator();
            while (iterPath.hasNext()) {
                String path = iterPath.next();
                if (path.equals(picturePath)) {
                    iterPath.remove();
                }
            }
            // Set new paths for AlbumData object
            albumData.setPicturePaths(paths);
            // Remove that AlbumData in total album data
            //TODO:
            //data.removeIf(d -> d.getAlbumName().equals(albumName));
            Iterator<AlbumData> iterData = data.iterator();
            while (iterData.hasNext()) {
                AlbumData adata = iterData.next();
                if (adata.getAlbumName().equals(albumName)) {
                    iterData.remove();
                }
            }
            // Add modified AlbumData object to data
            data.add(albumData);

            // Apply changes to shared preferences
            setAllAlbumData(data);
            return true;
        }
        return false;
    }

    public boolean deleteAllPicturesInAlbum(String albumName) {
        // Get AlbumData list and the AlbumData matching the name
        ArrayList<AlbumData> data = getAllAlbumData();
        AlbumData albumData = findDataByAlbumName(albumName);
        if (data != null && albumData != null) {
            // Empty the picturePaths
            albumData.setPicturePaths(new ArrayList<String>());
            // Add it to the data
            // TODO:
            data.removeIf(d -> d.getAlbumName().equals(albumName));
            Iterator<AlbumData> iter = data.iterator();
            while (iter.hasNext()) {
                AlbumData element = iter.next();
                if (element.getAlbumName().equals(albumName)) {
                    iter.remove();
                }
            }

            data.add(albumData);
            setAllAlbumData(data);
            return true;
        }
        return false;
    }

    public boolean deletePictureInAllAlbums(String picturePath) {
        ArrayList<AlbumData> data = getAllAlbumData();
        if (data != null) {
            for(AlbumData d : data) {
                //TODO:
                ArrayList<String> paths = d.getPicturePaths();
                Iterator<String> iter = paths.iterator();
                while (iter.hasNext()) {
                    String path = iter.next();
                    if (path.equals(picturePath)) {
                       iter.remove();
                    }
                }
                //paths.removeIf(path -> path.equals(picturePath));
                d.setPicturePaths(paths);
            }
            setAllAlbumData(data);
            return true;
        }
        return false;
    }

    public AlbumData findDataByAlbumName(String albumName) {
        ArrayList<AlbumData> data = getAllAlbumData();
        if (null != data)
            for (AlbumData d : data)
                if (d.getAlbumName().equals(albumName))
                    return d;
        return null;
    }

    public boolean checkPictureInFavorite(String picturePath) {
        AlbumData data = findDataByAlbumName("Favorite");
        if (data != null) {
            ArrayList<String> paths = data.getPicturePaths();
            for(String path: paths)
                if (path.equals(picturePath))
                    return true;
        }
        return false;
    }

    //Hide the picutre which has picturePath
    public boolean hide(String picturePath) {
        String currentFilename = picturePath.substring(picturePath.lastIndexOf('/') + 1);
        String newFilename = ".hide" + currentFilename;

        File directory = new File(picturePath.substring(0, picturePath.lastIndexOf('/')));
        File from = new File(directory, currentFilename);
        File to = new File(directory, newFilename);

        deletePictureInAllAlbums(from.getAbsolutePath());
        if (from.renameTo(to)) {
            addPictureToAlbum("Hide", to.getAbsolutePath());
            return true;
        }
        return false;
    }

    public boolean hidePictureInAlbum(String albumName, String picturePath) {
        // Get all album data
        ArrayList<AlbumData> data = getAllAlbumData();
        // Get AlbumData object matching the name
        AlbumData albumData = findDataByAlbumName(albumName);
        if (albumData != null) {

            // Remove required path in AlbumData object
            ArrayList<String> paths = albumData.getPicturePaths();
            //TODO:
            //paths.removeIf(s -> s.equals(picturePath));
            Iterator<String> iterPath = paths.iterator();
            while (iterPath.hasNext()) {
                String path = iterPath.next();
                if (path.equals(picturePath)) {
                    iterPath.remove();
                }
            }
            // Set new paths for AlbumData object
            albumData.setPicturePaths(paths);
            // Remove that AlbumData in total album data
            //TODO:
            //data.removeIf(d -> d.getAlbumName().equals(albumName));
            Iterator<AlbumData> iterData = data.iterator();
            while (iterData.hasNext()) {
                AlbumData adata = iterData.next();
                if (adata.getAlbumName().equals(albumName)) {
                    iterData.remove();
                }
            }
            // Add modified AlbumData object to data
            data.add(albumData);

            // Apply changes to shared preferences
            setAllAlbumData(data);
            return true;
        }
        return false;
    }

    public boolean unhide(String path) {
        String oldFilename = path.substring(path.lastIndexOf('/') + 1);
        String newFilename = oldFilename.replace(".hide", "");
        File directory = new File(path.substring(0, path.lastIndexOf('/')));
        File from = new File(directory, oldFilename);
        File to = new File(directory, newFilename);
        deletePictureInAlbum("Hide", from.getAbsolutePath());

        if (!from.renameTo(to))
        {
            return false;
        }

        return true;
    }
}
