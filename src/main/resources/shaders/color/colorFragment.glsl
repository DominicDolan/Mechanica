#version 400 core

in vec4 pass_color;

out vec4 out_Color;

void main(void){
	//out_Color = texture(textureSampler, pass_textureCoords);

	out_Color = pass_color;

}