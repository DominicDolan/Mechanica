#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;

void main(void){
 //   vec4 color = shaders.texture(textureSampler, pass_textureCoords);
 //   if (color.w > 0.5) out_Color = color;
 //   else out_Color = vec4(0,0,0,0);
	out_Color = texture(textureSampler, pass_textureCoords);
//    out_Color = vec4(1, 1, 1, 1);
}