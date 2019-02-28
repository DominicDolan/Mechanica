#version 400 core

in vec3 position;

out vec4 pass_color;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 color;

void main(void){
//projectionMatrix * viewMatrix * transformationMatrix *
//    gl_Position = vec4(position.x, position.y, position.z, 1.0);
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position.x, position.y, position.z, 1.0);
	pass_color = color;

}