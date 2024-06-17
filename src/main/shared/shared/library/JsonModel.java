package shared.library;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class JsonModel {
    Gson gson;

    public JsonModel(Gson gson) {
        this.gson = gson;
    }

    //Funcao que pega os dados do arquivo JSON
    public List<Book> getDataFromJson(String path) {
        try(FileReader fileReader = new FileReader(path)){
            Type bookListType = new TypeToken<List<Book>>(){}.getType();
            return gson.fromJson(fileReader, bookListType);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("I/O Error");
            throw new RuntimeException(e);
        }
    }

    public void saveDataToJson(ArrayList<Book> books, String path){
        try(FileWriter fileWriter = new FileWriter(path)){
           gson.toJson(books, fileWriter);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
