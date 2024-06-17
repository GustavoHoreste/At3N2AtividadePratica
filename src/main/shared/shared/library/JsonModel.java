package shared.library;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonModel {
    private static final String path = "src/main/resources/livros.json";
    Gson gson;

    public JsonModel() {
        this.gson = new Gson();
    }

    //Funcao que pega os dados do arquivo JSON
    public JsonObject getDataFromJson(){
        try(FileReader fileReader = new FileReader(path)){
            System.out.println(this.gson.fromJson(fileReader, JsonObject.class));
            return this.gson.fromJson(fileReader, JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}