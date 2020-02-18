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
    public int vertexCount;

    public void set(int characterCount){
        int dataLength = characterCount * 12;
        this.vertexCount = characterCount*6;
        ByteBuffer bb1 = ByteBuffer.allocateDirect(dataLength *4);
        bb1.order(ByteOrder.nativeOrder());
        vertices = bb1.asFloatBuffer();

        ByteBuffer bb2 = ByteBuffer.allocateDirect(dataLength *4);
        bb2.order(ByteOrder.nativeOrder());
        textureCoords = bb2.asFloatBuffer();
    }

    void putVertices(float[] characterVertices){
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
