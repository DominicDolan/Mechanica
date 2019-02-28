package font;

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 * @author Karl
 *
 */
public class TextMeshData {
	private static final int dataPoints = 512;
	private float[] vertexPositions;
	private float[] textureCoords;
	private int dataLength;

	protected TextMeshData(){
		this.vertexPositions = new float[dataPoints];
		this.textureCoords = new float[dataPoints];
	}

	protected TextMeshData(float[] vertexPositions, float[] textureCoords){
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public float getVertexIndex(int index){
		return vertexPositions[index];
	}


	public float getTextureCoordsIndex(int index){
		return textureCoords[index];
	}


	public void setVertexIndex(int index, float value){
		vertexPositions[index] = value;
	}


	public void setTextureCoordsIndex(int index, float value){
		textureCoords[index] = value;
	}

	public float[] getVertexPositions() {
		return vertexPositions;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public int getVertexCount() {
		return vertexPositions.length/2;
	}

}
