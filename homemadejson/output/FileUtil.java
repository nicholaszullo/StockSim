package homemadejson.output;

import homemadejson.support.InputDataBuffer;

import java.io.FileReader;
import java.io.IOException;

/**
 * File accessor
 */

public class FileUtil {

    public static InputDataBuffer readFile(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        InputDataBuffer inputDataBuffer = new InputDataBuffer(8192);
        inputDataBuffer.length = reader.read(inputDataBuffer.data);
        reader.close();
        return inputDataBuffer;
    }
}
