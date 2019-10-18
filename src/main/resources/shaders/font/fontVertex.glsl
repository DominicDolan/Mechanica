#version 330

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;
uniform mat4 projection;
uniform mat4 view;
uniform mat4 transformation;

void main(void){

	gl_Position = projection * view * transformation * vec4((position + translation), 0.0, 1.0);
//	gl_Position = projectionAndView * vec4(position*0.1, 0.0, 1.0);
	pass_textureCoords = textureCoords;

}