package font;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by domin on 31 Mar 2017.
 */

public class TextBufferData {
    public FloatBuffer vertices;
    public FloatBuffer textureCoords;
    public int dataLength;
    public int vertexCount;
    float[] translatedPoints;

    public TextBufferData(){
        translatedPoints = new float[12];
//
//        this.dataLength = dataLength;
//        ByteBuffer bb1 = ByteBuffer.allocateDirect(dataLength*4);
//        bb1.order(ByteOrder.nativeOrder());
//        polygons = bb1.asFloatBuffer();
//
//        ByteBuffer bb2 = ByteBuffer.allocateDirect(dataLength*4);
//        bb2.order(ByteOrder.nativeOrder());
//        textureCoords = bb2.asFloatBuffer();
    }

    public void set(int characterCount){
        this.dataLength = characterCount*12;
        this.vertexCount = characterCount*6;
        ByteBuffer bb1 = ByteBuffer.allocateDirect(dataLength*4);
        bb1.order(ByteOrder.nativeOrder());
        vertices = bb1.asFloatBuffer();

        ByteBuffer bb2 = ByteBuffer.allocateDirect(dataLength*4);
        bb2.order(ByteOrder.nativeOrder());
        textureCoords = bb2.asFloatBuffer();
    }

    public void putVertices(float[] characterVertices){
//        for (int i = 0; i < 11; i+=2) {
//            translatedPoints[i] = characterVertices[i] + positionX;
//        }
//
//        for (int i = 1; i < 12; i+=2) {
//            translatedPoints[i] = characterVertices[i] + positionY;
//        }

        vertices.put(characterVertices);
    }

    public void putTextureCoords(float[] characterCoords){
        textureCoords.put(characterCoords);
    }

    public void flip(){
        textureCoords.flip();
        vertices.flip();
    }
}
